package io.github.nodgu.core_server.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nodgu.core_server.global.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean EmailService emailService;

    @Test
    @DisplayName("가입 여부와 상관없이 같은 응답을 주고, 재설정하면 기존 토큰이 무효가 되며 새 비밀번호로 로그인된다")
    void forgotAndReset() throws Exception {
        String email = "reset-" + UUID.randomUUID() + "@example.com";
        String oldPassword = "oldPassword1!";
        String newPassword = "newPassword1!";

        post("/api/v1/auth/register", credentials(email, oldPassword), 200);
        JsonNode login = json(post("/api/v1/auth/login", credentials(email, oldPassword), 200));
        String accessToken = login.path("data").path("accessToken").asText();
        String refreshToken = login.path("data").path("refreshToken").asText();

        // 가입된 이메일과 없는 이메일의 응답이 똑같다
        String known = post("/api/v1/auth/password/forgot", "{\"email\":\"" + email + "\"}", 200);
        String unknown = post("/api/v1/auth/password/forgot",
                "{\"email\":\"nobody-" + UUID.randomUUID() + "@example.com\"}", 200);
        assertEquals(known, unknown);
        assertTrue(json(known).path("success").asBoolean());

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendPasswordResetEmail(eq(email), tokenCaptor.capture());
        String resetToken = tokenCaptor.getValue();

        // 곧바로 다시 요청하면 메일을 또 보내지 않는다
        post("/api/v1/auth/password/forgot", "{\"email\":\"" + email + "\"}", 200);
        verify(emailService, times(1)).sendPasswordResetEmail(eq(email), anyString());

        // iat(초 단위)가 재설정 시각보다 확실히 앞서도록 1초 넘게 기다린다
        Thread.sleep(1100);
        post("/api/v1/auth/password/reset",
                "{\"token\":\"" + resetToken + "\",\"newPassword\":\"" + newPassword + "\"}", 200);

        // 같은 토큰은 다시 쓸 수 없다
        post("/api/v1/auth/password/reset",
                "{\"token\":\"" + resetToken + "\",\"newPassword\":\"another1!\"}", 400);

        // 재설정 전에 발급된 토큰은 거부된다
        assertTrue(mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + accessToken))
                .andReturn().getResponse().getStatus() >= 400);
        assertTrue(mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/v1/auth/refresh").header("Authorization", "Bearer " + refreshToken))
                .andReturn().getResponse().getStatus() >= 400);

        // 예전 비밀번호로는 로그인 실패, 새 비밀번호로는 성공하고 새 토큰은 동작한다
        post("/api/v1/auth/login", credentials(email, oldPassword), 400);
        String newAccess = json(post("/api/v1/auth/login", credentials(email, newPassword), 200))
                .path("data").path("accessToken").asText();
        assertEquals(200, mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + newAccess))
                .andReturn().getResponse().getStatus());
    }

    @Test
    @DisplayName("메일 발송이 실패해도 응답은 같다")
    void forgotMailFailureStillSameResponse() throws Exception {
        String email = "mailfail-" + UUID.randomUUID() + "@example.com";
        post("/api/v1/auth/register", credentials(email, "password1!"), 200);
        doThrow(new RuntimeException("smtp down")).when(emailService).sendPasswordResetEmail(eq(email), anyString());

        String body = post("/api/v1/auth/password/forgot", "{\"email\":\"" + email + "\"}", 200);
        assertTrue(json(body).path("success").asBoolean());
    }

    private String credentials(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    private String post(String url, String body, int expectedStatus) throws Exception {
        MvcResult r = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andReturn();
        String content = r.getResponse().getContentAsString();
        assertEquals(expectedStatus, r.getResponse().getStatus(), url + " -> " + content);
        return content;
    }

    private JsonNode json(String body) throws Exception {
        return objectMapper.readTree(body);
    }
}

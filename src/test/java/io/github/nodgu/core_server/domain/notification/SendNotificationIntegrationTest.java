package io.github.nodgu.core_server.domain.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import io.github.nodgu.core_server.domain.user.service.UserService;
import io.github.nodgu.core_server.global.service.FcmPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.notification.api-key=test-api-key")
class SendNotificationIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired DeviceRepository deviceRepository;
    @Autowired NotificationRepository notificationRepository;

    @MockBean FcmPushService fcmPushService;

    @Test
    @DisplayName("API 키가 없거나 틀리면 401이고 푸시도 기록도 남지 않는다")
    void rejectsWithoutValidKey() throws Exception {
        User user = newUser();
        long before = notificationRepository.count();

        for (String path : new String[] { "/api/v1/notification/sendNotification", "/api/v1/notification/sendNotification/" }) {
            assertEquals(401, send(post(path), user.getId()).getResponse().getStatus());
            assertEquals(401, send(post(path).header("X-API-Key", "wrong-key"), user.getId()).getResponse().getStatus());
        }

        verify(fcmPushService, never()).sendPushNotification(anyString(), anyString(), anyString());
        assertEquals(before, notificationRepository.count());
    }

    @Test
    @DisplayName("올바른 API 키면 JWT 없이도 사용자의 모든 기기로 한 번씩 푸시가 가고, 알림 설정 없이도 201이다")
    void sendsToAllDevices() throws Exception {
        User user = newUser();
        userService.registerDevice(user, "token-phone-" + user.getId());
        userService.registerDevice(user, "token-tablet-" + user.getId());
        userService.registerDevice(user, "token-phone-" + user.getId()); // 같은 기기 재등록 → 중복 행 없음
        assertEquals(2, deviceRepository.findAllByUser(user).size());

        for (String path : new String[] { "/api/v1/notification/sendNotification", "/api/v1/notification/sendNotification/" }) {
            reset(fcmPushService);
            MvcResult result = send(post(path).header("X-API-Key", "test-api-key"), user.getId());
            assertEquals(201, result.getResponse().getStatus(), result.getResponse().getContentAsString());
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            assertTrue(body.path("success").asBoolean());

            verify(fcmPushService, times(1)).sendPushNotification(eq("token-phone-" + user.getId()), eq("제목"), eq("내용"));
            verify(fcmPushService, times(1)).sendPushNotification(eq("token-tablet-" + user.getId()), eq("제목"), eq("내용"));
            verifyNoMoreInteractions(fcmPushService);
        }
    }

    @Test
    @DisplayName("한 기기에서 다른 계정으로 로그인해 등록하면 이전 계정 알림은 그 기기로 가지 않는다")
    void deviceMovesToNewAccount() throws Exception {
        User oldUser = newUser();
        User newUser = newUser();
        String token = "shared-" + UUID.randomUUID();
        userService.registerDevice(oldUser, token);
        userService.registerDevice(newUser, token);

        assertTrue(deviceRepository.findAllByUser(oldUser).isEmpty());
        assertEquals(1, deviceRepository.findAllByUser(newUser).size());

        assertEquals(201, send(post("/api/v1/notification/sendNotification").header("X-API-Key", "test-api-key"),
                oldUser.getId()).getResponse().getStatus());
        verify(fcmPushService, never()).sendPushNotification(eq(token), anyString(), anyString());
    }

    private MvcResult send(MockHttpServletRequestBuilder req, Long userId) throws Exception {
        return mockMvc.perform(req.contentType(MediaType.APPLICATION_JSON)
                .content("{\"user_id\":" + userId + ",\"title\":\"제목\",\"description\":\"내용\"}")).andReturn();
    }

    private User newUser() {
        return userService.createUser("noti-" + UUID.randomUUID() + "@example.com", "password1!", "사용자");
    }
}

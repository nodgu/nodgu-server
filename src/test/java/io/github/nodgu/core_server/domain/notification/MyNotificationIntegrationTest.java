package io.github.nodgu.core_server.domain.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;
import io.github.nodgu.core_server.domain.user.entity.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.notification.api-key=test-api-key")
class MyNotificationIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserService userService;
    @Autowired NoticeRepository noticeRepository;
    @Autowired KeywordRepository keywordRepository;

    @MockBean FcmPushService fcmPushService;

    @Test
    @DisplayName("공지가 연결된 알림도 내 알림 목록과 전송 응답에 정상으로 담기고, 다른 사용자 정보는 나가지 않는다")
    void myNotificationsWithNotice() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        // 공지에 다른 사용자의 키워드가 연결된 상황
        User owner = userService.createUser("owner-" + suffix + "@example.com", "password1!", "키워드주인");
        Keyword keyword = new Keyword("장학" + suffix);
        keyword.setUser(owner);
        keyword = keywordRepository.save(keyword);

        Notice notice = new Notice();
        notice.setTitle("장학 공지 " + suffix);
        notice.setUrl("https://example.com/n");
        notice.setNotitype("general");
        notice.setNoticeId("n-" + suffix);
        notice.setDate(LocalDateTime.now());
        notice.setUnivCode("u");
        notice.setOrgCode("o");
        notice.setImgs(List.of("https://example.com/a.png"));
        notice.getKeywords().add(keyword);
        notice = noticeRepository.save(notice);

        String email = "me-" + suffix + "@example.com";
        String credentials = "{\"email\":\"" + email + "\",\"password\":\"password1!\"}";
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(credentials));
        String token = objectMapper.readTree(mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andReturn().getResponse().getContentAsString()).path("data").path("accessToken").asText();
        Long myId = userService.findByEmail(email).getId();

        // 공지 연결 알림 전송 → 201
        MvcResult sent = mockMvc.perform(post("/api/v1/notification/sendNotification")
                .header("X-API-Key", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user_id\":" + myId + ",\"title\":\"t1\",\"description\":\"d1\",\"notice_id\":" + notice.getId() + "}"))
                .andReturn();
        assertEquals(201, sent.getResponse().getStatus(), sent.getResponse().getContentAsString());

        // 공지 없는 알림도 하나
        mockMvc.perform(post("/api/v1/notification/sendNotification")
                .header("X-API-Key", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user_id\":" + myId + ",\"title\":\"t2\",\"description\":\"d2\"}"));

        MvcResult result = mockMvc.perform(get("/api/v1/notification/myNotification")
                .header("Authorization", "Bearer " + token)).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(200, result.getResponse().getStatus(), content);

        JsonNode list = objectMapper.readTree(content).path("data");
        assertEquals(2, list.size());

        JsonNode withNotice = list.get(0).path("notice").isObject() ? list.get(0) : list.get(1);
        JsonNode n = withNotice.path("notice");
        assertEquals(notice.getId().longValue(), n.path("id").asLong());
        assertEquals(notice.getTitle(), n.path("title").asText());
        assertEquals("장학" + suffix, n.path("keywords").get(0).asText());
        assertEquals("https://example.com/a.png", n.path("imgs").get(0).asText());

        JsonNode withoutNotice = withNotice == list.get(0) ? list.get(1) : list.get(0);
        assertTrue(withoutNotice.path("notice").isNull());

        // 키워드 주인의 정보가 응답에 섞이지 않는다
        assertFalse(content.contains("owner-" + suffix), content);
        assertFalse(content.contains("password"), content);
    }
}

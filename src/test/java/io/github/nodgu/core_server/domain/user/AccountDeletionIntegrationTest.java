package io.github.nodgu.core_server.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.notification.repository.NotificationSettingRepository;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;
import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;
import io.github.nodgu.core_server.domain.sub.repository.ScrapRepository;
import io.github.nodgu.core_server.domain.sub.repository.SearchHistoryRepository;
import io.github.nodgu.core_server.domain.user.entity.PasswordResetToken;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import io.github.nodgu.core_server.domain.user.repository.PasswordResetTokenRepository;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import io.github.nodgu.core_server.global.schedule.notificationSchedule;
import io.github.nodgu.core_server.global.service.FcmPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountDeletionIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired DeviceRepository deviceRepository;
    @Autowired PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired KeywordRepository keywordRepository;
    @Autowired ScrapRepository scrapRepository;
    @Autowired SearchHistoryRepository searchHistoryRepository;
    @Autowired NotificationRepository notificationRepository;
    @Autowired NotificationSettingRepository notificationSettingRepository;
    @Autowired NoticeRepository noticeRepository;
    @Autowired notificationSchedule schedule;
    @Autowired TransactionTemplate transactionTemplate;

    @MockBean FcmPushService fcmPushService;

    private static final String PASSWORD = "password123!";

    @Test
    @DisplayName("탈퇴하면 계정과 관련 데이터가 모두 삭제되고, 로그인·기존 토큰·푸시가 막히며, 같은 이메일로 재가입된다")
    void quitDeletesEverything() throws Exception {
        String email = "quit-" + UUID.randomUUID() + "@example.com";
        String keywordTitle = "탈퇴테스트" + UUID.randomUUID().toString().substring(0, 8);

        register(email);
        JsonNode login = login(email);
        String accessToken = login.path("data").path("accessToken").asText();
        String refreshToken = login.path("data").path("refreshToken").asText();
        User user = userRepository.findByEmail(email).orElseThrow();
        Long userId = user.getId();

        // 기기 FCM 토큰은 API로 등록
        mockMvc.perform(post("/api/v1/auth/device")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fcmToken\":\"fcm-" + userId + "\"}"))
                .andExpect(r -> assertEquals(200, r.getResponse().getStatus()));

        // 나머지 사용자 데이터는 저장소로 직접 생성
        Notice notice = new Notice();
        notice.setTitle("장학 공지");
        notice.setUrl("https://example.com/n/" + userId);
        notice.setNotitype("general");
        notice.setNoticeId("n-" + UUID.randomUUID());
        notice.setDate(LocalDateTime.now().minusMinutes(10));
        notice.setTdindex("이번 학기 " + keywordTitle + " 안내");
        notice.setUnivCode("u");
        notice.setOrgCode("o");
        notice = noticeRepository.save(notice);

        Keyword keyword = new Keyword(keywordTitle);
        keyword.setUser(user);
        keyword = keywordRepository.save(keyword);

        notice.getKeywords().add(keyword);
        noticeRepository.save(notice);

        // NotificationSetting은 keyword에 PERSIST cascade가 있어 관리 상태의 엔티티로 저장해야 한다
        Long keywordId = keyword.getId();
        Long noticePk = notice.getId();
        transactionTemplate.executeWithoutResult(tx -> {
            Keyword managedKeyword = keywordRepository.findById(keywordId).orElseThrow();
            User managedUser = userRepository.findById(userId).orElseThrow();
            NotificationSetting setting = notificationSettingRepository.save(NotificationSetting.builder()
                    .keyword(managedKeyword).alarmDays(127).alarmTime("09:00").user(managedUser).build());
            notificationRepository.save(Notification.builder()
                    .title("t").description("d").remindDate(LocalDateTime.now())
                    .user(managedUser).notice(noticeRepository.findById(noticePk).orElseThrow())
                    .notificationSetting(setting).build());
        });
        scrapRepository.save(Scrap.builder().user(user).notice(notice).build());
        searchHistoryRepository.save(new SearchHistory(user, "장학"));
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(resetToken);

        // 탈퇴 전에는 키워드 알림 푸시가 실제로 나간다 (테스트 경로 확인)
        schedule.sendKeywordNoticeSchedule();
        verify(fcmPushService, atLeastOnce()).sendPushNotification(eq("fcm-" + userId), anyString(), anyString());

        // 비밀번호가 틀리면 탈퇴되지 않는다
        mockMvc.perform(post("/api/v1/auth/quit")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"wrong-password\"}"))
                .andExpect(r -> assertEquals(400, r.getResponse().getStatus()));
        assertTrue(userRepository.existsById(userId));

        // 탈퇴: 응답은 기존과 같은 200 + success:true
        MvcResult quit = mockMvc.perform(post("/api/v1/auth/quit")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"" + PASSWORD + "\"}"))
                .andReturn();
        assertEquals(200, quit.getResponse().getStatus());
        JsonNode quitBody = objectMapper.readTree(quit.getResponse().getContentAsString());
        assertTrue(quitBody.path("success").asBoolean());
        assertEquals(200, quitBody.path("status").asInt());

        // 계정과 관련 데이터가 모두 지워졌다
        assertFalse(userRepository.existsById(userId));
        assertFalse(userRepository.existsByEmail(email));
        assertTrue(keywordRepository.findByUser_Id(userId).isEmpty());
        assertEquals(0, scrapRepository.findAll().stream().filter(s -> s.getUser().getId().equals(userId)).count());
        assertEquals(0, deviceRepository.findAll().stream().filter(d -> d.getUser().getId().equals(userId)).count());
        assertEquals(0, searchHistoryRepository.findAll().stream().filter(h -> h.getUser().getId().equals(userId)).count());
        assertEquals(0, notificationRepository.findAll().stream().filter(n -> n.getUser().getId().equals(userId)).count());
        assertEquals(0, notificationSettingRepository.findAll().stream().filter(n -> n.getUser().getId().equals(userId)).count());
        assertEquals(0, passwordResetTokenRepository.findAll().stream().filter(t -> t.getUser().getId().equals(userId)).count());
        assertTrue(noticeRepository.findById(notice.getId()).isPresent(), "공지 자체는 지워지면 안 된다");

        // 탈퇴한 계정으로 로그인 불가
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email)))
                .andExpect(r -> assertEquals(400, r.getResponse().getStatus()));

        // 기존 액세스/리프레시 토큰으로 API 호출 불가
        assertRejected(get("/api/v1/auth/me"), accessToken);
        assertRejected(get("/api/v1/sub/myKeyword"), accessToken);
        assertRejected(get("/api/v1/sub/myScrap"), accessToken);
        assertRejected(post("/api/v1/auth/refresh"), refreshToken);

        // 탈퇴한 사람 기기로 키워드 알림 푸시가 더 이상 나가지 않는다
        reset(fcmPushService);
        schedule.sendKeywordNoticeSchedule();
        verify(fcmPushService, never()).sendPushNotification(eq("fcm-" + userId), anyString(), anyString());

        // 같은 이메일로 다시 가입 가능
        register(email);
        Long newUserId = userRepository.findByEmail(email).orElseThrow().getId();
        assertNotEquals(userId, newUserId);

        // 재가입 후에도 예전 토큰으로는 새 계정에 접근할 수 없다
        assertRejected(get("/api/v1/auth/me"), accessToken);
        assertRejected(post("/api/v1/auth/refresh"), refreshToken);
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + login(email).path("data").path("accessToken").asText()))
                .andExpect(r -> assertEquals(200, r.getResponse().getStatus()));
    }

    @Test
    @DisplayName("isActive=false(이용 정지) 계정은 로그인과 기존 토큰 사용이 막힌다")
    void suspendedUserIsBlocked() throws Exception {
        String email = "suspended-" + UUID.randomUUID() + "@example.com";
        register(email);
        String accessToken = login(email).path("data").path("accessToken").asText();

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setIsActive(false);
        userRepository.save(user);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email)))
                .andExpect(r -> assertEquals(400, r.getResponse().getStatus()));
        assertRejected(get("/api/v1/auth/me"), accessToken);
    }

    private void assertRejected(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder req,
            String token) throws Exception {
        MvcResult result = mockMvc.perform(req.header("Authorization", "Bearer " + token)).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status >= 400, "거부되어야 하는데 " + status + " 응답: " + result.getResponse().getContentAsString());
    }

    private String credentials(String email) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}";
    }

    private void register(String email) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email))).andReturn();
        assertEquals(200, r.getResponse().getStatus(), r.getResponse().getContentAsString());
    }

    private JsonNode login(String email) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email))).andReturn();
        assertEquals(200, r.getResponse().getStatus(), r.getResponse().getContentAsString());
        return objectMapper.readTree(r.getResponse().getContentAsString());
    }
}

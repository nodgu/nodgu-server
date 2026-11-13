package io.github.nodgu.core_server.global.schedule;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class KeywordNotificationScheduleTest {

    @Autowired notificationSchedule schedule;
    @Autowired UserService userService;
    @Autowired KeywordRepository keywordRepository;
    @Autowired NoticeRepository noticeRepository;

    @MockBean FcmPushService fcmPushService;

    @Test
    @DisplayName("전날 공지 중 tdindex에 키워드가 있는 공지를 키워드 등록 사용자 모두에게 한 번씩 알린다")
    void sendsYesterdayKeywordNotices() {
        // 다른 테스트 데이터와 겹치지 않도록 먼 미래 날짜와 고유 키워드를 쓴다
        LocalDate target = LocalDate.of(2099, 3, 15);
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        String kw = "장학" + suffix;          // "장학xxxxxx"
        String kwLong = kw + "금";            // "장학xxxxxx금" (kw를 포함)

        User a = user(); // kw
        User b = user(); // kw
        User c = user(); // kw + kwLong → 같은 공지는 한 번만
        User d = user(); // 다른 키워드
        keyword(a, kw);
        keyword(b, kw);
        keyword(c, kw);
        keyword(c, kwLong);
        keyword(d, "없는키워드" + suffix);
        for (User u : new User[] { a, b, c, d }) {
            userService.registerDevice(u, "tok-" + u.getId());
        }

        Notice hit = notice(target.atTime(13, 0), "2099 " + kwLong + " 신청 안내", "본문 " + kwLong + " 신청");
        notice(target.plusDays(1).atTime(0, 0), "다음날 공지", "본문 " + kw);      // 범위 밖 (다음날 00:00)
        notice(target.minusDays(1).atTime(23, 59), "전전날 공지", "본문 " + kw);   // 범위 밖
        notice(target.atTime(10, 0), "키워드 없는 공지", "관련 없는 내용");

        schedule.sendKeywordNotificationsFor(target);

        Map<String, String> data = Map.of("type", "notice", "notice_id", String.valueOf(hit.getId()));
        verify(fcmPushService).sendPushNotification("tok-" + a.getId(), "\"" + kw + "\"의 공지를 찾았습니다!", hit.getTitle(), data);
        verify(fcmPushService).sendPushNotification("tok-" + b.getId(), "\"" + kw + "\"의 공지를 찾았습니다!", hit.getTitle(), data);
        verify(fcmPushService, times(1)).sendPushNotification(eq("tok-" + c.getId()), anyString(), eq(hit.getTitle()), eq(data));
        verify(fcmPushService, never()).sendPushNotification(eq("tok-" + d.getId()), anyString(), anyString(), anyMap());
        verifyNoMoreInteractions(fcmPushService);
    }

    private User user() {
        return userService.createUser("sched-" + UUID.randomUUID() + "@example.com", "password1!", "사용자");
    }

    private void keyword(User user, String title) {
        Keyword k = new Keyword(title);
        k.setUser(user);
        keywordRepository.save(k);
    }

    private Notice notice(java.time.LocalDateTime date, String title, String tdindex) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setUrl("https://example.com/n");
        n.setNotitype("general");
        n.setNoticeId("n-" + UUID.randomUUID());
        n.setDate(date);
        n.setTdindex(tdindex);
        n.setUnivCode("u");
        n.setOrgCode("o");
        return noticeRepository.save(n);
    }
}

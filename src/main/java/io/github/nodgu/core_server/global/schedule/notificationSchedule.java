package io.github.nodgu.core_server.global.schedule;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notification.service.NotificationService;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class notificationSchedule {

    private static final Logger log = LoggerFactory.getLogger(notificationSchedule.class);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final KeywordRepository keywordRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationService notificationService;

    public notificationSchedule(KeywordRepository keywordRepository, NoticeRepository noticeRepository,
            NotificationService notificationService) {
        this.keywordRepository = keywordRepository;
        this.noticeRepository = noticeRepository;
        this.notificationService = notificationService;
    }

    /**
     * 매일 오전 9시(한국 시간)에 전날 공지 중 키워드가 tdindex에 들어간 공지를 키워드 등록 사용자에게 알린다.
     * 서버 시간대(UTC)와 상관없이 한국 시간 기준으로 돈다.
     */
    @Scheduled(cron = "${app.notification.keyword-cron:0 0 9 * * *}", zone = "Asia/Seoul")
    public void sendDailyKeywordNotifications() {
        sendKeywordNotificationsFor(LocalDate.now(KST).minusDays(1));
    }

    /**
     * targetDate 하루(00:00 이상 다음날 00:00 미만) 동안의 공지를 대상으로 알림을 보낸다.
     * 한 사용자가 같은 공지에 걸리는 키워드를 여러 개 갖고 있어도 알림은 한 번만 보낸다.
     */
    public void sendKeywordNotificationsFor(LocalDate targetDate) {
        LocalDateTime since = targetDate.atStartOfDay();
        LocalDateTime until = targetDate.plusDays(1).atStartOfDay();

        // 1) 키워드 제목별로 등록한 사용자 묶기
        Map<String, Set<Long>> usersByKeyword = new TreeMap<>();
        for (Keyword keyword : keywordRepository.findAll()) {
            String title = keyword.getTitle() == null ? "" : keyword.getTitle().trim();
            if (title.isEmpty() || keyword.getUser() == null || keyword.getUser().getId() == null) {
                continue;
            }
            usersByKeyword.computeIfAbsent(title, k -> new LinkedHashSet<>()).add(keyword.getUser().getId());
        }

        // 2) 키워드마다 전날 공지를 찾아 사용자에게 알림
        Set<String> sent = new HashSet<>(); // "userId:noticeId" 중복 방지
        int sentCount = 0;
        int failedCount = 0;
        for (Map.Entry<String, Set<Long>> entry : usersByKeyword.entrySet()) {
            String keyword = entry.getKey();
            List<Long> noticeIds = noticeRepository.findRecentNoticeIdsByKeyword(keyword, since, until);
            if (noticeIds.isEmpty()) {
                continue;
            }

            for (Long noticeId : noticeIds) {
                Notice notice = noticeRepository.findById(noticeId).orElse(null);
                if (notice == null) {
                    continue;
                }
                for (Long userId : entry.getValue()) {
                    if (!sent.add(userId + ":" + noticeId)) {
                        continue;
                    }
                    try {
                        notificationService.addNotification(new NotificationRequest(
                                userId,
                                "\"" + keyword + "\"의 공지를 찾았습니다!",
                                notice.getTitle(),
                                null,
                                notice.getId(),
                                LocalDateTime.now(KST)));
                        sentCount++;
                    } catch (Exception e) {
                        failedCount++;
                        log.warn("키워드 알림 전송 실패 userId={} noticeId={} keyword={}", userId, noticeId, keyword, e);
                    }
                }
            }
        }

        log.info("키워드 알림 완료 date={} keywords={} sent={} failed={}",
                targetDate, usersByKeyword.size(), sentCount, failedCount);
    }

    // ---------------------------------------------------------------------------
    // [이전 버전] 매시 정각에 최근 1시간 공지를 검사하던 스케줄러 (참고용으로 남겨 둠)
    // 하루 한 번 전날 공지를 보내는 sendDailyKeywordNotifications()로 대체했다.
    // 바꾼 이유:
    //  - LocalDateTime.now()가 서버 시간대(UTC) 기준이라 한국 시간 공지 date와 어긋날 수 있음
    //  - 공지 date에 시간이 없거나 크롤러가 늦게 넣으면 1시간 구간에 걸리지 않음
    //  - 한 사용자가 같은 공지에 걸리는 키워드를 여러 개 가지면 중복 알림
    // 두 스케줄러를 같이 켜면 중복 알림이 가므로 다시 쓰려면 새 스케줄러를 끄고 주석을 풀 것.
    // ---------------------------------------------------------------------------
    // @Scheduled(cron = "0 0 * * * *")
    // public void sendKeywordNoticeSchedule() {
    //     // 변경: 전체 키워드를 DB에서 전부 조회하여 Set에 적재
    //     // Set<Long> keywordIds = new HashSet<>();
    //     // Set<String> keywordTitles = new HashSet<>();
    //     List<Keyword> allKeywords = keywordRepository.findAll();
    //     // for (Keyword keyword : allKeywords) {
    //     // if (keyword.getId() != null)
    //     // keywordIds.add(keyword.getId());
    //     // if (keyword.getTitle() != null)
    //     // keywordTitles.add(keyword.getTitle());
    //     // }
    //
    //     // 최근 1시간 내 공지를 키워드별로 조회 후, 해당 키워드의 소유 사용자에게 알림 전송
    //     java.time.LocalDateTime now = java.time.LocalDateTime.now();
    //     java.time.LocalDateTime since = now.minusHours(1);
    //     for (Keyword keyword : allKeywords) {
    //         String kw = keyword.getTitle();
    //         if (kw == null || kw.isBlank() || keyword.getUser() == null || keyword.getUser().getId() == null)
    //             continue;
    //
    //         List<Long> noticeIds = noticeRepository.findRecentNoticeIdsByKeyword(kw, since, now);
    //         for (Long noticeId : noticeIds) {
    //             Notice notice = noticeRepository.findById(noticeId).orElse(null);
    //             if (notice == null)
    //                 continue;
    //
    //             NotificationRequest req = new NotificationRequest(
    //                     keyword.getUser().getId(),
    //                     "키워드 알림: " + kw,
    //                     notice.getTitle(),
    //                     null, // notificationSettingId 없음(전역 키워드 기반)
    //                     notice.getId(),
    //                     now);
    //             notificationService.addNotification(req);
    //         }
    //     }
    // }
}

package io.github.nodgu.core_server.global.schedule;

import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notification.service.NotificationService;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class notificationSchedule {

    private final KeywordRepository keywordRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationService notificationService;

    public notificationSchedule(KeywordRepository keywordRepository, NoticeRepository noticeRepository,
            NotificationService notificationService) {
        this.keywordRepository = keywordRepository;
        this.noticeRepository = noticeRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendKeywordNoticeSchedule() {
        // 변경: 전체 키워드를 DB에서 전부 조회하여 Set에 적재
        // Set<Long> keywordIds = new HashSet<>();
        // Set<String> keywordTitles = new HashSet<>();
        List<Keyword> allKeywords = keywordRepository.findAll();
        // for (Keyword keyword : allKeywords) {
        // if (keyword.getId() != null)
        // keywordIds.add(keyword.getId());
        // if (keyword.getTitle() != null)
        // keywordTitles.add(keyword.getTitle());
        // }

        // 최근 1시간 내 공지를 키워드별로 조회 후, 해당 키워드의 소유 사용자에게 알림 전송
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime since = now.minusHours(1);
        for (Keyword keyword : allKeywords) {
            String kw = keyword.getTitle();
            if (kw == null || kw.isBlank() || keyword.getUser() == null || keyword.getUser().getId() == null)
                continue;

            List<Long> noticeIds = noticeRepository.findRecentNoticeIdsByKeyword(kw, since, now);
            for (Long noticeId : noticeIds) {
                Notice notice = noticeRepository.findById(noticeId).orElse(null);
                if (notice == null)
                    continue;

                NotificationRequest req = new NotificationRequest(
                        keyword.getUser().getId(),
                        "키워드 알림: " + kw,
                        notice.getTitle(),
                        null, // notificationSettingId 없음(전역 키워드 기반)
                        notice.getId(),
                        now);
                notificationService.addNotification(req);
            }
        }
    }

    /**
     * 주어진 DayOfWeek가 포함된 모든 요일 비트마스크 조합의 정수 배열을 반환합니다.
     * 예: DayOfWeek.MONDAY를 입력하면, 월요일이 포함된 모든 조합(1~127 중 월요일 비트가 켜진 값)을 반환합니다.
     */
    public int[] getAllCombinationsIncludingDay(java.time.DayOfWeek dayOfWeek) {
        int dayBit;
        // DayOfWeek는 1(월)~7(일) 이므로, 비트마스크는 0(일)~6(토)로 매핑
        if (dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            dayBit = 0;
        } else {
            dayBit = dayOfWeek.getValue();
        }
        int mask = 1 << dayBit;
        java.util.List<Integer> result = new java.util.ArrayList<>();
        for (int i = 1; i < 128; i++) { // 1~127까지 모든 조합
            if ((i & mask) != 0) {
                result.add(i);
            }
        }
        // int 배열로 변환
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}

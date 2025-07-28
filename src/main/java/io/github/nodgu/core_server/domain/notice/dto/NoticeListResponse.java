package io.github.nodgu.core_server.domain.notice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

public class NoticeListResponse {
    private List<NoticeDto> notices;
    private long totalCount;
    private int currentPage;

    public NoticeListResponse(List<NoticeDto> notices, long totalCount, int currentPage) {
        this.notices = notices;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
    }

    public List<NoticeDto> getNotices() {
        return notices;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public static class NoticeDto {
        private Long id;
        private String noticeId;
        private String title;
        private String description;
        private String url;
        private String notitype;
        private LocalDateTime date;
        private List<String> keywords;

        public NoticeDto(Long id, String noticeId, String title, String description, String url, String notitype, LocalDateTime date, List<String> keywords) {
            this.id = id;
            this.noticeId = noticeId;
            this.title = title;
            this.description = description;
            this.url = url;
            this.notitype = notitype;
            this.date = date;
            this.keywords = keywords;
        }

        public static NoticeDto from(Notice notice) {
            List<String> keywordNames = notice.getKeywords().stream()
                .map(Keyword::getTitle)
                .collect(Collectors.toList());

            return new NoticeDto(
                notice.getId(),
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getDescription(),
                notice.getUrl(),
                notice.getNotitype(),
                notice.getDate(),
                keywordNames
            );
        }

        public Long getId() { return id; }
        public String getNoticeId() { return noticeId; }
        public String getTitle() { return title; }
        public String getDescription() {return description; }
        public String getUrl() { return url; }
        public String getNotitype() { return notitype; }
        public LocalDateTime getDate() { return date; }
        public List<String> getKeywords() { return keywords; }
    }

    public static NoticeListResponse from(List<Notice> notices, long totalCount, int currentPage) {
        List<NoticeDto> dtoList = notices.stream()
            .map(NoticeDto::from)
            .collect(Collectors.toList());

        return new NoticeListResponse(dtoList, totalCount, currentPage);
    }
}
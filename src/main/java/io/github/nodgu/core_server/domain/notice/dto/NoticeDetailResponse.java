package io.github.nodgu.core_server.domain.notice.dto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;

public class NoticeDetailResponse {
    private Long id;
    private String noticeId;
    private String title;
    private String url;
    private String notitype;
    private String description;
    private LocalDateTime date;
    private String tdindex;
    private List<String> imgs;
    private List<String> links;
    private List<Map<String, Object>> attachments;
    private String ocrData;
    private String univCode;
    private String orgCode;
    private String subCode;
    private List<String> keywords;

    public NoticeDetailResponse() {}

    public NoticeDetailResponse(Long id, String noticeId, String title, String url, String notitype,
                                String description, LocalDateTime date, String tdindex, List<String> imgs,
                                List<String> links, List<Map<String, Object>> attachments, String ocrData,
                                String univCode, String orgCode, String subCode, List<String> keywords) {
        this.id = id;
        this.noticeId = noticeId;
        this.title = title;
        this.url = url;
        this.notitype = notitype;
        this.description = description;
        this.date = date;
        this.tdindex = tdindex;
        this.imgs = imgs;
        this.links = links;
        this.attachments = attachments;
        this.ocrData = ocrData;
        this.univCode = univCode;
        this.orgCode = orgCode;
        this.subCode = subCode;
        this.keywords = keywords;
    }

    public static NoticeDetailResponse from(Notice notice) {
        List<String> keywordNames = notice.getKeywords().stream()
            .map(Keyword::getTitle)
            .collect(Collectors.toList());

        return new NoticeDetailResponse(
            notice.getId(),
            notice.getNoticeId(),
            notice.getTitle(),
            notice.getUrl(),
            notice.getNotitype(),
            notice.getDescription(),
            notice.getDate(),
            notice.getTdindex(),
            notice.getImgs(),
            notice.getLinks(),
            notice.getAttachments(),
            notice.getOcrData(),
            notice.getUnivCode(),
            notice.getOrgCode(),
            notice.getSubCode(),
            keywordNames
        );
    }

    // Getter들 추가
    public Long getId() {
        return id;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getNotitype() {
        return notitype;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTdindex() {
        return tdindex;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public List<String> getLinks() {
        return links;
    }

    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }

    public String getOcrData() {
        return ocrData;
    }

    public String getUnivCode() {
        return univCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getSubCode() {
        return subCode;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}

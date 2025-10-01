package io.github.nodgu.core_server.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

public class NoticeRequest {

    @NotBlank(message = "공지ID는 필수입니다")
    private String noticeId;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "URL은 필수입니다")
    private String url;

    @NotBlank(message = "공지 내용은 필수입니다")
    private String description;

    @NotBlank(message = "공지 타입은 필수입니다")
    private String notitype;

    @NotBlank(message = "날짜는 필수입니다")
    private LocalDateTime date;

    private String tdindex;

    private List<String> imgs;

    private List<String> links;

    private List<Map<String, Object>> attachments;

    private String ocrData;

    @NotBlank(message = "대학 코드는 필수입니다")
    private String univCode;

    @NotBlank(message = "단과대 코드는 필수입니다")
    private String orgCode;

    private String subCode;

    // 기본 생성자
    public NoticeRequest() {}

    // Getter/Setter
    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotitype() {
        return notitype;
    }

    public void setNotitype(String notitype) {
        this.notitype = notitype;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTdindex() {
        return tdindex;
    }

    public void setTdindex(String tdindex) {
        this.tdindex = tdindex;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Map<String, Object>> attachments) {
        this.attachments = attachments;
    }

    public String getOcrData() {
        return ocrData;
    }

    public void setOcrData(String ocrData) {
        this.ocrData = ocrData;
    }

    public String getUnivCode() {
        return univCode;
    }

    public void setUnivCode(String univCode) {
        this.univCode = univCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }
}

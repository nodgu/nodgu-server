package io.github.nodgu.core_server.domain.notice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;

@Entity
@Table(
    name = "NOTICE",
    uniqueConstraints = @UniqueConstraint(columnNames = {"univCode", "orgCode", "subCode", "noticeId"})
)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String notitype;

    @Lob
    private String description;

    @Column(nullable = false)
    private String noticeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Lob
    private String tdindex;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> imgs;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> links;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> attachments;

    @Lob
    private String ocrData;

    @Column(nullable = false)
    private String univCode;

    @Column(nullable = false)
    private String orgCode;

    @Column(nullable = true)
    private String subCode;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "notice_keyword",
        joinColumns = @JoinColumn(name = "notice_id"),
        inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private List<Keyword> keywords = new ArrayList<>();

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    // 기본 생성자
    public Notice() {}

    // 생성자
    public Notice(String noticeId, String title, String url, String description, String notitype,
                  LocalDateTime date, String tdindex, List<String> imgs, List<String> links,
                  List<Map<String, Object>> attachments, String ocrData,
                  String univCode, String orgCode, String subCode) {
        this.noticeId = noticeId;
        this.title = title;
        this.url = url;
        this.description = description;
        this.notitype = notitype;
        this.date = date;
        this.tdindex = tdindex;
        this.imgs = imgs;
        this.links = links;
        this.attachments = attachments;
        this.ocrData = ocrData;
        this.univCode = univCode;
        this.orgCode = orgCode;
        this.subCode = subCode;
    }

    // Getter/Setter
    public Long getId() {
        return id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDefinition(String description) {
        this.description = description;
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

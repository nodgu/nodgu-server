package io.github.nodgu.core_server.domain.notice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.github.nodgu.core_server.domain.notice.dto.NoticeRequest;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;

@Entity
@Getter
@Setter
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

    public void update(NoticeRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.notitype = request.getNotitype();
        this.date = request.getDate();
        this.tdindex = request.getTdindex();
        this.imgs = request.getImgs();
        this.links = request.getLinks();
        this.attachments = request.getAttachments();
        this.ocrData = request.getOcrData();
        this.univCode = request.getUnivCode();
        this.orgCode = request.getOrgCode();
        this.subCode = request.getSubCode();
    }
}

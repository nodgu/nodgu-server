package io.github.nodgu.core_server.domain.notification.entity;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword", nullable = false)
    private Keyword keyword;

    @Column(name = "title")
    private String title;

    @Column(name = "description") // 공지 내용 요약(or 키워드 해당 부분 잘라낸거)
    private String description;

    @Column(name = "remind_date")
    private LocalDateTime remindDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    // 어떤 notificationSetting에서 왔는지 알아야 할 것 같아서 추가했음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_setting")
    private NotificationSetting notificationSetting; // 이거 추가함에 따라 노션 DB Schema 수정했으니 검토 필요

    @Builder
    public Notification(Keyword keyword, String title, String description, LocalDateTime remindDate, User user, Notice notice, NotificationSetting notificationSetting) {
        this.keyword = keyword;
        this.title = title;
        this.description = description;
        this.remindDate = remindDate;
        this.user = user;
        this.notice = notice;
        this.notificationSetting = notificationSetting;
    }
}
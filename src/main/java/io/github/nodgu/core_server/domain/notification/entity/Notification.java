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
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "keyword", nullable = false)
    // private Keyword keyword;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_setting")
    private NotificationSetting notificationSetting;

    @Builder
    public Notification(String title, String description, LocalDateTime remindDate, User user, Notice notice, NotificationSetting notificationSetting) {
        this.title = title;
        this.description = description;
        this.remindDate = remindDate;
        this.user = user;
        this.notice = notice;
        this.notificationSetting = notificationSetting;
    }
}
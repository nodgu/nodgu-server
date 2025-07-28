package io.github.nodgu.core_server.domain.notification.entity;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "title") // 공지 제목.. 얘네도 공지 가져오면 안 됨?
    private String title;

    @Column(name = "description") // 공지 내용 요약(or 키워드 해당 부분 잘라낸거)
    private Long description;

    @Column(name = "remind_date")
    private int remind_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice")
    private Notice notice;

    // 어떤 notification_setting에서 왔는지 알아야 할 것 같아서 추가했음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_setting")
    private NotificationSetting notification_setting; // 이거 추가함에 따라 노션 DB Schema 수정했으니 검토 필요

    @Builder
    public Notification(String keyword, String title, Long description, int remind_date, User user, Notice notice, NotificationSetting notificationSetting) {
        this.keyword = keyword;
        this.title = title;
        this.description = description;
        this.remind_date = remind_date;
        this.user = user;
        this.notice = notice;
        this.notification_setting = notification_setting;
    }
}
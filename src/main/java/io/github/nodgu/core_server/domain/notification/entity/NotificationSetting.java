package io.github.nodgu.core_server.domain.notification.entity;

import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
// import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_setting")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(name = "title")
    private String title;

    @Column(name = "alarm_days")
    private Integer alarmDays;

    @Column(name = "alarm_time")
    private String alarmTime;

    @Column(name = "is_active")
    private Boolean is_active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public NotificationSetting(Keyword keyword, String title, Integer alarmDays, String alarmTime, User user) {
        this.keyword = keyword;
        if (title == null || title.trim().isEmpty()) this.title = keyword.getTitle(); //기본값 keyword
        else this.title = title;
        this.alarmDays = alarmDays;
        this.alarmTime = alarmTime;
        this.user = user;
        this.is_active = is_active;
    }
}
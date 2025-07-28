package io.github.nodgu.core_server.domain.notification.entity;

import io.github.nodgu.core_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_setting")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "title")
    private String title;

    @Column(name = "alarm_days")
    private int alarm_days;

    @Column(name = "alarm_time")
    private LocalDateTime alarm_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @Builder
    public NotificationSetting(String keyword, String title, int alarm_days, LocalDateTime alarm_time, User user) {
        this.keyword = keyword;
        if (title == null || title.trim().isEmpty()) this.title = keyword; //기본값 keyword
        else this.title = title;
        this.alarm_days = alarm_days;
        this.alarm_time = alarm_time;
        this.user = user;
    }
}
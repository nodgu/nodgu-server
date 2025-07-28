package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationSettingResponse {
    private final Long id;
    private final String title;
    private final String keyword;
    private final int alarm_days;
    private final LocalDateTime alarm_time;

    public NotificationSettingResponse(NotificationSetting notification_setting) {
        this.id = notification_setting.getId();
        this.title = notification_setting.getTitle();
        this.keyword = notification_setting.getKeyword();
        this.alarm_days = notification_setting.getAlarmDays();
        this.alarm_time = notification_setting.getAlarmTime();
    }
}
package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import java.time.LocalDateTime;

// 근데 이게 NotificationSettingResponse랑 다른 게 뭔가요? 둘이 똑같은데 그냥 Controller에서만 다르면 되는 거 아닌가..
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationSettingListResponse {
    private final Long id;
    private final String title;
    private final String keyword;
    private final int alarm_days;
    private final LocalDateTime alarm_time;

    public NotificationSettingListResponse(NotificationSetting notification_setting) {
        this.id = notification_setting.getId();
        this.title = notification_setting.getTitle();
        this.keyword = notification_setting.getKeyword();
        this.alarm_days = notification_setting.getAlarmDays();
        this.alarm_time = notification_setting.getAlarmTime();
    }
}
package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationSettingResponse {
    private Long id;
    private String title;
    private Keyword keyword;
    private Integer alarmDays;
    private String alarmTime;

    public NotificationSettingResponse(NotificationSetting notificationSetting) {
        this.id = notificationSetting.getId();
        this.title = notificationSetting.getTitle();
        this.keyword = notificationSetting.getKeyword();
        this.alarmDays = notificationSetting.getAlarmDays();
        this.alarmTime = notificationSetting.getAlarmTime();
    }
}
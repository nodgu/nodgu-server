package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationListResponse {
    private Long id;
    private String title;
    private String description;
    private Notice notice;
    private Long notificationSettingId;

    public NotificationListResponse(Notification notification) {
        this.id = notification.getId();
        this.notice = notification.getNotice();
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.notificationSettingId = notification.getNotificationSetting().getId();
    }
}
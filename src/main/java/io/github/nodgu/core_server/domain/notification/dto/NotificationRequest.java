package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String description;

    @JsonProperty("notification_setting_id")
    private Long notificationSettingId;

    @JsonProperty("remind_date")
    private LocalDateTime remindDate;
}
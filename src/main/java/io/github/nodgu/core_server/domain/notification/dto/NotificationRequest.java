package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    @JsonProperty("user_id")
    private Long userId;
    private String title;
    private String description;

    @JsonProperty("notification_setting_id")
    private Long notificationSettingId;

    @JsonProperty("notice_id")
    private Long noticeId = null;

    @JsonProperty("remind_date")
    private LocalDateTime remindDate;
}
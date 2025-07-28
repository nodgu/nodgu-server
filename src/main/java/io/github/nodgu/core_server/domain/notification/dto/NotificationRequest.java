package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    @JsonProperty("user_id")
    private Long user_id;

    private String title;
    private String description;
    private String keyword;

    @JsonProperty("remind_date")
    private LocalDateTime remind_date;
}
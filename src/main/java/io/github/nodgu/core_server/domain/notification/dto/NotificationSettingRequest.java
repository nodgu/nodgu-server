package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationSettingRequest {
    @JsonProperty("user_id")
    private Long user_id;

    private String title; // 설정된 알림 제목
    private String keyword;
    private int alarm_days;

    @JsonProperty("alarm_time")
    private LocalDateTime alarm_time;
}
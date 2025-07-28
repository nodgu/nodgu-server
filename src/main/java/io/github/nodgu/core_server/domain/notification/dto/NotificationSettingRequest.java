package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationSettingRequest {
    private String title; // 설정된 알림 제목
    private Keyword keyword;
    private Integer alarmDays;

    @JsonProperty("alarmTime")
    private String alarmTime;
}
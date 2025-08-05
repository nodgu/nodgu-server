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

    @JsonProperty("keyword_id")
    private Long keywordId;
    
    @JsonProperty("alarm_days")
    private Integer alarmDays;

    @JsonProperty("alarm_time")
    private String alarmTime;
}
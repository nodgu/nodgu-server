package io.github.nodgu.core_server.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {
    @NotBlank(message = "FCM 토큰은 필수입니다")
    private String fcmToken;

    public DeviceRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public DeviceRequest() {}
} 
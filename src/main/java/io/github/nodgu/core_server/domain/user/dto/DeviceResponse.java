package io.github.nodgu.core_server.domain.user.dto;

import io.github.nodgu.core_server.domain.user.entity.Device;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponse {
    private Long id;
    private String fcmToken;
    private Long userId;

    public DeviceResponse(Long id, String fcmToken, Long userId) {
        this.id = id;
        this.fcmToken = fcmToken;
        this.userId = userId;
    }

    public DeviceResponse(Device device) {
        this.id = device.getId();
        this.fcmToken = device.getFcmToken();
        this.userId = device.getUser().getId();
    }

    public DeviceResponse() {}

} 
package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingResponse;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.service.NotificationSettingService;
import io.github.nodgu.core_server.domain.user.entity.User;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification/myNotificationSetting")
public class NotificationSettingApiController {

    private final NotificationSettingService notificationSettingService;

    @PostMapping
    public ResponseEntity<NotificationSettingResponse> addNotificationSetting(
            @RequestBody NotificationSettingRequest request,
            @AuthenticationPrincipal User user) {

        NotificationSetting savedSetting = notificationSettingService.addNotificationSetting(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationSettingResponse(savedSetting));
    }

    @GetMapping("/myNotificationSetting")
    public ResponseEntity<List<NotificationSettingResponse>> findAllNotificationSettings(
            @AuthenticationPrincipal User user) {

        List<NotificationSettingResponse> settings = notificationSettingService.findAllNotificationSettings(user.getId())
                .stream()
                .map(NotificationSettingResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(settings);
    }

    @GetMapping("/myNotificationSetting/{id}")
    public ResponseEntity<NotificationSettingResponse> getNotificationSetting(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {

        NotificationSetting setting = notificationSettingService.findById(id);

        if (!setting.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("접근 권한이 없습니다.");
        }

        return ResponseEntity.ok()
                .body(new NotificationSettingResponse(setting));
    }

    @PatchMapping("/myNotificationSetting/{id}")
    public ResponseEntity<NotificationSettingResponse> updateNotificationSetting(
            @PathVariable("id") Long id,
            @RequestBody NotificationSettingRequest request,
            @AuthenticationPrincipal User user) {

        NotificationSetting updatedSetting = notificationSettingService.updateNotificationSetting(id, user.getId(), request);

        return ResponseEntity.ok()
                .body(new NotificationSettingResponse(updatedSetting));
    }

    @DeleteMapping("/myNotificationSetting/{id}")
    public ResponseEntity<Void> deleteNotificationSetting(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {

        notificationSettingService.deleteNotificationSetting(id, user.getId());

        return ResponseEntity.ok()
                .build();
    }
}
package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingResponse;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.service.NotificationSettingService;
import io.github.nodgu.core_server.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.github.nodgu.core_server.global.annotation.CurrentUser;

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
            @CurrentUser User user) {

        NotificationSetting savedSetting = notificationSettingService.addNotificationSetting(request, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationSettingResponse(savedSetting));
    }

    @GetMapping
    public ResponseEntity<List<NotificationSettingResponse>> findAllNotificationSettings(
            @CurrentUser User user) {

        List<NotificationSettingResponse> settings = notificationSettingService.findAllNotificationSettings(user)
                .stream()
                .map(NotificationSettingResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(settings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationSettingResponse> getNotificationSetting(
            @PathVariable("id") Long id,
            @CurrentUser User user) {

        NotificationSetting setting = notificationSettingService.findById(id, user);

        return ResponseEntity.ok()
                .body(new NotificationSettingResponse(setting));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NotificationSettingResponse> updateNotificationSetting(
            @PathVariable("id") Long id,
            @RequestBody NotificationSettingRequest request,
            @CurrentUser User user) {

        NotificationSetting updatedSetting = notificationSettingService.updateNotificationSetting(id, request, user);

        return ResponseEntity.ok()
                .body(new NotificationSettingResponse(updatedSetting));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationSetting(
            @PathVariable("id") Long id,
            @CurrentUser User user) {

        notificationSettingService.deleteNotificationSetting(id, user);

        return ResponseEntity.ok()
                .build();
    }
}
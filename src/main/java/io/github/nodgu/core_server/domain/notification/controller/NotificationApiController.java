package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationListResponse;
import io.github.nodgu.core_server.domain.notification.service.NotificationService;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import io.github.nodgu.core_server.global.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/myNotification")
    public ResponseEntity<ApiResponse<List<NotificationListResponse>>> findAllNotifications(
            @CurrentUser User user) {
        List<NotificationListResponse> notifications = notificationService.findAllNotifications(user)
                .stream()
                .map(NotificationListResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("알림 조회 성공", notifications));
    }

    @PostMapping("/sendNotification/")
    public ResponseEntity<ApiResponse<NotificationListResponse>> addNotification(
            @RequestBody NotificationRequest request) {

        Notification savedNotification = notificationService.addNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("알림 추가 성공", new NotificationListResponse(savedNotification)));
    }

    @DeleteMapping("/myNotification/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable("id") Long id, @CurrentUser User user) {
        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok(ApiResponse.success("알림 삭제 성공", null));
    }
}
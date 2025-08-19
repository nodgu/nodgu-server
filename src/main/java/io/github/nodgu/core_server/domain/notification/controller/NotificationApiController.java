package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationListResponse;
import io.github.nodgu.core_server.domain.notification.service.NotificationService;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.global.annotation.CurrentUser;

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
    public ResponseEntity<List<NotificationListResponse>> findAllNotifications(
            @CurrentUser User user) {
        List<NotificationListResponse> notifications = notificationService.findAllNotifications(user)
                .stream()
                .map(NotificationListResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(notifications);
    }

    @PostMapping("/sendNotification/")
    public ResponseEntity<NotificationListResponse> addNotification(
            @RequestBody NotificationRequest request) {

        Notification savedNotification = notificationService.addNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationListResponse(savedNotification));
    }

    @DeleteMapping("/myNotification/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id, @CurrentUser User user) {
        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok()
                .build();
    }
}
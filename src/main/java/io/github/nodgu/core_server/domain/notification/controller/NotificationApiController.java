package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationResponse;
import io.github.nodgu.core_server.domain.notification.service.NotificationService;
import io.github.nodgu.core_server.domain.user.entity.User;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationApiController {

    private final NotificationService notificationService;
    @GetMapping("/myNotification")
    public ResponseEntity<List<NotificationResponse>> findAllNotifications(
            @AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.findAllNotifications(user.getId())
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(notifications);
    }

    @PostMapping("/myNotification")
    public ResponseEntity<NotificationResponse> addNotification(
            @RequestBody NotificationRequest request,
            @AuthenticationPrincipal User user) {

        Notification savedNotification = notificationService.addNotification(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationResponse(savedNotification));
    }

    @DeleteMapping("/myNotification/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        notificationService.deleteNotification(id, user.getId());
        return ResponseEntity.ok()
                .build();
    }
}
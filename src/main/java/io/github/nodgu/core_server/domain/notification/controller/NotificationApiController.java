package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    private static final String API_KEY_HEADER = "X-API-Key";

    private final NotificationService notificationService;

    @Value("${app.notification.api-key:}")
    private String notificationApiKey;

    // 키가 설정되지 않았으면 모든 요청을 거부한다. 길이 정보가 새지 않도록 상수 시간 비교를 쓴다.
    private boolean isValidApiKey(String apiKey) {
        if (notificationApiKey == null || notificationApiKey.isBlank() || apiKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                notificationApiKey.getBytes(StandardCharsets.UTF_8),
                apiKey.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/myNotification")
    public ResponseEntity<ApiResponse<List<NotificationListResponse>>> findAllNotifications(
            @CurrentUser User user) {
        List<NotificationListResponse> notifications = notificationService.findAllNotifications(user)
                .stream()
                .map(NotificationListResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("알림 조회 성공", notifications));
    }

    /**
     * 외부 서버(크롤러 등)에서 호출하는 알림 전송 API.
     * 로그인 대신 X-API-Key 헤더가 서버에 설정된 키(NODGU_NOTIFICATION_API_KEY)와 같아야 한다.
     */
    @PostMapping({ "/sendNotification", "/sendNotification/" })
    public ResponseEntity<ApiResponse<NotificationListResponse>> addNotification(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @RequestBody NotificationRequest request) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("유효하지 않은 API 키입니다", 401));
        }

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
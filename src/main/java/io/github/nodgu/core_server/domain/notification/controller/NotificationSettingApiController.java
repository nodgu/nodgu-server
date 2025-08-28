package io.github.nodgu.core_server.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingRequest;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingResponse;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.service.NotificationSettingService;
import io.github.nodgu.core_server.domain.user.entity.User;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import io.github.nodgu.core_server.global.dto.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification/myNotificationSetting")
public class NotificationSettingApiController {

        private final NotificationSettingService notificationSettingService;

        @PostMapping
        public ResponseEntity<ApiResponse<NotificationSettingResponse>> addNotificationSetting(
                        @RequestBody NotificationSettingRequest request,
                        @CurrentUser User user) {

                NotificationSetting savedSetting = notificationSettingService.addNotificationSetting(request, user);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("알림 설정 추가 성공",
                                                new NotificationSettingResponse(savedSetting)));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<NotificationSettingResponse>>> findAllNotificationSettings(
                        @CurrentUser User user) {

                List<NotificationSettingResponse> settings = notificationSettingService
                                .findAllNotificationSettings(user)
                                .stream()
                                .map(NotificationSettingResponse::new)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(ApiResponse.success("알림 설정 조회 성공", settings));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSetting(
                        @PathVariable("id") Long id,
                        @CurrentUser User user) {

                NotificationSetting setting = notificationSettingService.findById(id, user);

                return ResponseEntity.ok()
                                .body(ApiResponse.success("알림 설정 조회 성공", new NotificationSettingResponse(setting)));
        }

        @PatchMapping("/{id}")
        public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateNotificationSetting(
                        @PathVariable("id") Long id,
                        @RequestBody NotificationSettingRequest request,
                        @CurrentUser User user) {

                NotificationSetting updatedSetting = notificationSettingService.updateNotificationSetting(id, request,
                                user);

                return ResponseEntity.ok()
                                .body(ApiResponse.success("알림 설정 수정 성공",
                                                new NotificationSettingResponse(updatedSetting)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteNotificationSetting(
                        @PathVariable("id") Long id,
                        @CurrentUser User user) {

                notificationSettingService.deleteNotificationSetting(id, user);

                return ResponseEntity.ok(ApiResponse.success("알림 설정 삭제 성공", null));
        }
}
package io.github.nodgu.core_server.domain.notification.service;

import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.repository.NotificationSettingRepository;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import io.github.nodgu.core_server.global.service.FcmPushService;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final FcmPushService fcmPushService;
    private final DeviceRepository deviceRepository;

    @Transactional
    public Notification addNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // DTO의 ID로 NotificationSetting 엔티티 조회 (null 허용)
        NotificationSetting notificationSetting = null;
        if (request.getNotificationSettingId() != null) {
            notificationSetting = notificationSettingRepository
                    .findById(request.getNotificationSettingId())
                    .orElseThrow(() -> new IllegalArgumentException("알림 설정을 찾을 수 없습니다."));
        }

        // noticeId가 null일 수 있으므로 조건부로 처리
        Notice notice = null;
        if (request.getNoticeId() != null) {
            notice = noticeRepository.findById(request.getNoticeId())
                    .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        }

        Notification new_notification = Notification.builder()
                .user(user)
                .notificationSetting(notificationSetting)
                .notice(notice) // null일 수 있음
                .title(request.getTitle())
                .description(request.getDescription())
                .remindDate(request.getRemindDate())
                .build();

        Notification saved = notificationRepository.save(new_notification);

        // 공지와 연결된 알림이면 앱이 알림을 눌렀을 때 그 공지를 열 수 있도록 공지 ID를 함께 보낸다
        java.util.Map<String, String> data = saved.getNotice() != null
                ? java.util.Map.of("type", "notice", "notice_id", String.valueOf(saved.getNotice().getId()))
                : java.util.Map.of();

        // 사용자가 등록한 모든 기기로 푸시 전송 (같은 토큰은 한 번만, 기기가 없으면 기록만 저장)
        deviceRepository.findAllByUser(user).stream()
                .map(Device::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .distinct()
                .forEach(token -> fcmPushService.sendPushNotification(token, saved.getTitle(),
                        saved.getDescription(), data));

        return saved;
    }

    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + id));
    }

    public List<Notification> findAllNotifications(User user) {
        return notificationRepository.findByUser(user);
    }

    @Transactional
    public void deleteNotification(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 알림을 찾을 수 없습니다. ID: " + id));
        notificationRepository.deleteById(id);
    }
}
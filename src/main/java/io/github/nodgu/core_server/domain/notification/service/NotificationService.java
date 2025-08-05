package io.github.nodgu.core_server.domain.notification.service;

import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.repository.NotificationSettingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationSettingRepository notificationSettingRepository; 

    @Transactional
    public Notification addNotification(NotificationRequest request, User user) {
        // DTO의 ID로 NotificationSetting 엔티티 조회
        NotificationSetting notificationSetting = notificationSettingRepository.findById(request.getNotificationSettingId())
                .orElseThrow(() -> new IllegalArgumentException("알림 설정을 찾을 수 없습니다."));
        
        Notification new_notification = Notification.builder()
                .user(user)
                .notificationSetting(notificationSetting)
                .title(request.getTitle())
                .description(request.getDescription())
                .remindDate(request.getRemindDate())
                .build();

        return notificationRepository.save(new_notification);
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
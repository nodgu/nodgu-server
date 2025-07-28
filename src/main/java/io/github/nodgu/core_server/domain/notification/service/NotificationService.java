package io.github.nodgu.core_server.domain.notification.service;

import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.notification.dto.NotificationRequest;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;

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

    @Transactional
    public Notification addNotification(Long user, NotificationRequest request) {
        User user = userRepository.findById(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user));

        Notice notice = noticeRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. ID: " + request.getNoticeId()));

        Notification new_notification = Notification.builder()
                .user(user)
                .notice(notice)
                .keyword(request.getKeyword())
                .title(request.getTitle())
                .description(request.getDescription())
                .remindDate(request.getRemindDate())
                .build();

        return notificationRepository.save(new_notification);
    }

    public Notification findById(Long notification_id) {
        return notificationRepository.findById(notification_id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + notification_id));
    }

    public List<Notification> findAllNotifications(Long user) {
        User user = userRepository.findById(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user));

        return notificationRepository.findByUser(user);
    }

    @Transactional
    public void deleteNotification(Long notification_id, Long user) {
        Notification notification = notificationRepository.findById(notification_id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 알림을 찾을 수 없습니다. ID: " + notification_id));

        if (!notification.getUser().getId().equals(user)) {
            throw new IllegalArgumentException("이 알림을 삭제할 권한이 없습니다. (사용자 ID 불일치)");
        }

        notificationRepository.deleteById(notification_id);
    }
}
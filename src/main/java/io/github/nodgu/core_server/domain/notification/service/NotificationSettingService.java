package io.github.nodgu.core_server.domain.notification.service;

import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import io.github.nodgu.core_server.domain.notification.repository.NotificationSettingRepository;
import io.github.nodgu.core_server.domain.notification.dto.NotificationSettingRequest;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public NotificationSetting addNotificationSetting(NotificationSettingRequest request, User user) {
        NotificationSetting newNotificationSetting = NotificationSetting.builder()
                .user(user)
                .keyword(request.getKeyword())
                .title(request.getTitle())
                .alarmDays(request.getAlarmDays())
                .alarmTime(request.getAlarmTime())
                .build();

        return notificationSettingRepository.save(newNotificationSetting);
    }

    public NotificationSetting findById(Long id, User user) {
        return notificationSettingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + id));
    }

    public List<NotificationSetting> findAllNotificationSettings(User user) {
        return notificationSettingRepository.findByUser(user);
    }
    
    @Transactional
    public NotificationSetting updateNotificationSetting(Long id, NotificationSettingRequest request, User user) {
        NotificationSetting notificationSetting = notificationSettingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 알림을 찾을 수 없습니다. ID: " + id));

        if (request.getKeyword() != null) {
            notificationSetting.setKeyword(request.getKeyword());
        }
        if (request.getTitle() != null) {
            notificationSetting.setTitle(request.getTitle());
        }
        if (request.getAlarmDays() != null) {
            notificationSetting.setAlarmDays(request.getAlarmDays());
        }
        if (request.getAlarmTime() != null) {
            notificationSetting.setAlarmTime(request.getAlarmTime());
        }
        return notificationSetting;
    }

    @Transactional
    public void deleteNotificationSetting(Long id, User user) {
        notificationSettingRepository.deleteById(id);
    }
}
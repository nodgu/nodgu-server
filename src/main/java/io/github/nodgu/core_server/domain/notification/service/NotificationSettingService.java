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

    private final NotificationSettingRepository NotificationSettingRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public NotificationSetting addNotificationSetting(Long user_id, NotificationSettingRequest request) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user_id));

        NotificationSetting new_notification_setting = NotificationSetting.builder()
                .user(user)
                .keyword(request.getKeyword())
                .title(request.getTitle())
                .alarm_days(request.getAlarmDays())
                .alarm_time(request.getAlarmTime())
                .build();

        return NotificationSettingRepository.save(new_notification_setting);
    }

    public NotificationSetting findById(Long notification_setting_id) {
        return NotificationSettingRepository.findById(notification_setting_id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + NotificationSetting_id));
    }

    public List<NotificationSetting> findAllNotificationSettings(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user_id));

        return NotificationSettingRepository.findByUser(user);
    }
    
    @Transactional
    public NotificationSetting updateNotificationSetting(Long notification_setting_id, Long user_id, NotificationSettingRequest request) {
        NotificationSetting notification_setting = NotificationSettingRepository.findById(notification_setting_id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 알림을 찾을 수 없습니다. ID: " + notification_setting_id));

        if (!notification_setting.getUser().getId().equals(user_id)) {
            throw new IllegalArgumentException("이 알림을 수정할 권한이 없습니다. (사용자 ID 불일치)");
        }

        if (request.getKeyword() != null) {
            notification_setting.updateKeyword(request.getKeyword());
        }
        if (request.getTitle() != null) {
            notification_setting.updateTitle(request.getTitle());
        }
        if (request.getAlarmDays() != null) {
            notification_setting.updateAlarmDays(request.getAlarmDays());
        }
        if (request.getAlarmTime() != null) {
            notification_setting.updateAlarmTime(request.getAlarmTime());
        }
        return notification_setting;
    }

    @Transactional
    public void deleteNotificationSetting(Long notification_setting_id, Long user_id) {
        NotificationSetting notification_setting = NotificationSettingRepository.findById(notification_setting_id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 알림을 찾을 수 없습니다. ID: " + notification_setting_id));

        if (!NotificationSetting.getUser().getId().equals(user_id)) {
            throw new IllegalArgumentException("이 알림을 삭제할 권한이 없습니다. (사용자 ID 불일치)");
        }
        NotificationSettingRepository.deleteById(notification_setting_id);
    }
}
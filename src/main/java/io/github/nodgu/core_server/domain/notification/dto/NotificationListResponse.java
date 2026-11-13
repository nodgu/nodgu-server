package io.github.nodgu.core_server.domain.notification.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.notice.dto.NoticeDetailResponse;
import io.github.nodgu.core_server.domain.notification.entity.Notification;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationListResponse {
    private Long id;
    private String title;
    private String description;
    // 공지 엔티티를 그대로 내보내면 지연 로딩 프록시 직렬화가 실패하고,
    // keywords → user 를 따라가 다른 사용자 정보까지 나갈 수 있으므로 DTO로 바꿔 담는다
    private NoticeDetailResponse notice;
    private Long notificationSettingId;

    public NotificationListResponse(Notification notification) {
        this.id = notification.getId();
        this.notice = notification.getNotice() != null
                ? NoticeDetailResponse.from(notification.getNotice())
                : null;
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.notificationSettingId = notification.getNotificationSetting() != null
                ? notification.getNotificationSetting().getId()
                : null;
    }
}

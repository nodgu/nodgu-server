import lombok.*;
import io.github.nodgu.core_server.domain.notification.entity.Notification;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationRequest {
    private final Long id;
    private final Long user_id;
    private final String title;
    private final String description;
    private final LocalDateTime remind_date;

    public NotificationRequest(Notification notification) {
        this.id = notification.getId();
        this.user_id = notification.getUser().getId();
        Notice notice = scrap.getNotice();
        this.title = notice.getTitle(); // Notice 엔티티에 getTitle() 필수
        this.description = notice.getDescription(); // Notice 엔티티에 getContent() 필수
        this.remind_date = notice.getRemindDate();
    }
}
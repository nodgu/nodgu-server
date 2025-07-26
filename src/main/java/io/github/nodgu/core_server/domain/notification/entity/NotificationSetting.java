import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "title")
    private String title;

    @Column(name = "alarm_days")
    private int alarm_days; //이거 숫자로 하는 거라고 들었던 것 같은데 int 맞는지 확인 필요

    @Column(name = "alarm_time")
    private String alarm_time; // String 맞았나 이거

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public NotificationSetting(keyword, title, alarm_days, alarm_time, User user) {
        this.keyword = keyword;
        if (title == null || title.trim().isEmpty()) this.title = keyword; //기본값 keyword
        else this.title = title;
        this.alarm_days = alarm_days;
        this.alarm_time = alarm_time;
        this.user = user;
    }
}
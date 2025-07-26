import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "title") // 공지 제목.. 얘네도 공지 가져오면 안 됨?
    private String title;

    @Column(name = "description") // 공지 내용 요약(or 키워드 해당 부분 잘라낸거)
    private Long description;

    @Column(name = "remind_date") // 이거 그냥 알림 설정 들고 오면 안 됨?
    private int remind_date; // 이거 숫자로 하는 거라고 들었던 것 같은데 int 맞는지 확인 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public Notification(keyword, title, description, remind_date, User user, Notice notice) {
        this.keyword = keyword;
        this.title = title;
        this.description = description;
        this.remind_date = remind_date;
        this.user = user;
        this.notice = notice;
    }
}
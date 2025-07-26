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

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private Long description;

    @Column(name = "remind_date")
    private int remind_date; //이거 숫자로 하는 거라고 들었던 것 같은데 int 맞는지 확인 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public Notification(keyword, title, description, remind_date, User user, Notice notice) {
        this.keyword = keyword;
        if (title == null || title.trim().isEmpty()) this.title = keyword; //기본값 keyword
        else this.title = title;
        this.description = description;
        this.remind_date = remind_date;
        this.user = user;
        this.notice = notice;
    }
}
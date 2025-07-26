import lombok.*;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScrapListResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ScrapListResponse(Scrap scrap) {
        this.id = scrap.getId();
        
        Notice notice = scrap.getNotice();
        this.title = notice.getTitle(); // Notice 엔티티에 getTitle() 필수
        this.content = notice.getContent(); // Notice 엔티티에 getContent() 필수
    }
}
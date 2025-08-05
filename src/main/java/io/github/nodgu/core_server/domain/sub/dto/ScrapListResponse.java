package io.github.nodgu.core_server.domain.sub.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScrapListResponse {
    private Long id;
    private String title;
    private String description;

    public ScrapListResponse(Scrap scrap) {
        this.id = scrap.getId();
        this.title = scrap.getNotice().getTitle();
        this.description = scrap.getNotice().getDescription();
    }
}
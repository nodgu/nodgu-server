package io.github.nodgu.core_server.domain.sub.dto;

import lombok.*;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScrapListResponse {
    private final Long id;
    private final String title;
    private final Long description;

    public Scrap toEntity() {
        return Scrap.builder()
            .id(id)
            .title(title)
            .description(description)
            .build();
    }
}
package io.github.nodgu.core_server.domain.sub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScrapListResponse {
    private Long id;

    @JsonProperty("notice_id")
    private Long noticeId;

    private String title;
    private String description;

    public ScrapListResponse(Scrap scrap) {
        this.id = scrap.getId();
        this.noticeId = scrap.getNotice().getId();
        this.title = scrap.getNotice().getTitle();
        this.description = scrap.getNotice().getDescription();
    }
}
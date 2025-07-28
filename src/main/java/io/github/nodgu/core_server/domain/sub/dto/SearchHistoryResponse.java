package io.github.nodgu.core_server.domain.sub.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;

@Getter
@Setter
public class SearchHistoryResponse {
    private Long id;
    private String query;
    private LocalDateTime createdAt;

    public SearchHistoryResponse(SearchHistory searchHistory) {
        this.id = searchHistory.getId();
        this.query = searchHistory.getQuery();
        this.createdAt = searchHistory.getCreatedAt();
    }
}

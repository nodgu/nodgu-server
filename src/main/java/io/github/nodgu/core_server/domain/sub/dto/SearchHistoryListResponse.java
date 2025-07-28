package io.github.nodgu.core_server.domain.sub.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;

@Getter
@Setter
public class SearchHistoryListResponse {
    private List<SearchHistoryResponse> searchHistories;

    public SearchHistoryListResponse(List<SearchHistory> searchHistories) {
        this.searchHistories = searchHistories.stream()
            .map(SearchHistoryResponse::new)
            .collect(Collectors.toList());
    }
}

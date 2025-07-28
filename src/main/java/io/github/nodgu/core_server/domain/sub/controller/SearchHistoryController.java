package io.github.nodgu.core_server.domain.sub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nodgu.core_server.domain.sub.dto.SearchHistoryListResponse;
import io.github.nodgu.core_server.domain.sub.dto.SearchHistoryRequest;
import io.github.nodgu.core_server.domain.sub.dto.SearchHistoryResponse;
import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;
import io.github.nodgu.core_server.domain.sub.service.SearchHistoryService;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import io.github.nodgu.core_server.global.dto.ApiResponse;

@RestController
@RequestMapping("/api/v1/sub/search-history")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @Autowired
    public SearchHistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SearchHistoryResponse>> createSearchHistory(
        @RequestBody SearchHistoryRequest request,
        @CurrentUser User user
    ) {
        SearchHistory searchHistory = searchHistoryService.createSearchHistory(request, user);
        SearchHistoryResponse searchHistoryResponse = new SearchHistoryResponse(searchHistory);

        return ResponseEntity.ok(ApiResponse.success("검색 기록 생성 성공", searchHistoryResponse, 201));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<SearchHistoryListResponse>> getSearchHistories(
        @CurrentUser User user
    ) {
        List<SearchHistory> searchHistories = searchHistoryService.getSearchHistories(user);
        SearchHistoryListResponse searchHistoryListResponse = new SearchHistoryListResponse(searchHistories);
        return ResponseEntity.ok(ApiResponse.success("검색 기록 조회 성공", searchHistoryListResponse, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSearchHistory(
        @PathVariable Long id,
        @CurrentUser User user
    ) {
        searchHistoryService.deleteSearchHistory(id, user);
        return ResponseEntity.ok(ApiResponse.success("검색 기록 삭제 성공", null, 200));
    }
}

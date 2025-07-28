package io.github.nodgu.core_server.domain.sub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.nodgu.core_server.domain.sub.dto.SearchHistoryRequest;
import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;
import io.github.nodgu.core_server.domain.sub.repository.SearchHistoryRepository;
import io.github.nodgu.core_server.domain.user.entity.User;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    @Autowired
    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public SearchHistory createSearchHistory(SearchHistoryRequest request, User user) {
        if (user == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        SearchHistory searchHistory = new SearchHistory(user, request.getQuery());
        return searchHistoryRepository.save(searchHistory);
    }

    public List<SearchHistory> getSearchHistories(User user) {
        if (user == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Pageable pageable = PageRequest.of(0, 30);
        return searchHistoryRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public void deleteSearchHistory(Long id, User user) {
        if (user == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        SearchHistory searchHistory = searchHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("검색 기록을 찾을 수 없습니다."));
        if (!searchHistory.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("검색 기록을 삭제할 수 없습니다.");
        }
        searchHistoryRepository.delete(searchHistory);
    }
}

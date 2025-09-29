package io.github.nodgu.core_server.domain.sub.service;

import io.github.nodgu.core_server.domain.sub.dto.KeywordRequest;
import io.github.nodgu.core_server.domain.sub.dto.KeywordListResponse;
import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KeywordService {
    
    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public List<KeywordListResponse> getKeywordList(Long userId) {
        List<Keyword> keywords = keywordRepository.findByUser_Id(userId);
        return keywords.stream()
                .map(k -> new KeywordListResponse(k.getId(), k.getTitle(), k.getIsActive()))
                .collect(Collectors.toList());
    }

    public void createKeyword(Long userId, KeywordRequest request) {
        if (keywordRepository.existsByUser_IdAndTitle(userId, request.getTitle())) {
            throw new IllegalArgumentException("이미 존재하는 키워드입니다");
        }

        Keyword keyword = new Keyword();
        keyword.setTitle(request.getTitle());
        keyword.setUserId(userId);
        keyword.setIsActive(true);
        keywordRepository.save(keyword);
    }

    public void updateKeyword(Long userId, Long id, KeywordRequest request) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
        if (!keyword.getUser().getId().equals(userId)) {
            throw new SecurityException("권한이 없습니다");
        }
        keyword.setTitle(request.getTitle());
        if (request.getIsActive() != null) {
            keyword.setIsActive(request.getIsActive());
        }
        // keywordRepository.save(keyword);
    }

    public void deleteKeyword(Long userId, Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
        if (!keyword.getUser().getId().equals(userId)) {
            throw new SecurityException("권한이 없습니다.");
        }
        keywordRepository.delete(keyword);
    }
}
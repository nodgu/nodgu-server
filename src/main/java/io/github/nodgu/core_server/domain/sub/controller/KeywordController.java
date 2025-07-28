package io.github.nodgu.core_server.domain.sub.controller;

import io.github.nodgu.core_server.domain.sub.dto.KeywordRequest;
import io.github.nodgu.core_server.domain.sub.dto.KeywordListResponse;
import io.github.nodgu.core_server.domain.sub.service.KeywordService;
import io.github.nodgu.core_server.global.dto.ApiResponse;
import io.github.nodgu.core_server.global.util.JwtUtil;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sub")
public class KeywordController {
    
    private final KeywordService keywordService;
    private final JwtUtil jwtUtil;

    @Autowired
    public KeywordController (KeywordService keywordService, JwtUtil jwtUtil){
        this.keywordService = keywordService;
        this.jwtUtil = jwtUtil;
    }

     private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.extractUserId(token);
    }

    @GetMapping("/myKeyword")
    public ResponseEntity<ApiResponse<List<KeywordListResponse>>> getKeywords(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요한 기능입니다.", 401));
        }

        try {
            List<KeywordListResponse> list = keywordService.getKeywordList(userId);
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("키워드 목록 조회에 실패했습니다: " + e.getMessage(), 500));
        }
    }

    @PostMapping("/myKeyword")
    public ResponseEntity<ApiResponse<String>> createKeyword(@Valid @RequestBody KeywordRequest keywordRequest,
                                                             HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요한 기능입니다", 401));
        }

        try {
            keywordService.createKeyword(userId, keywordRequest);
            return ResponseEntity.ok(ApiResponse.success("키워드가 성공적으로 추가되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("키워드 추가에 실패했습니다: " + e.getMessage(), 500));
        }
    }

    @PatchMapping("/myKeyword/{id}")
    public ResponseEntity<ApiResponse<String>> updateKeyword(@PathVariable Long id,
                                                             @Valid @RequestBody KeywordRequest keywordRequest,
                                                             HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요한 기능입니다", 401));
        }

        try {
            keywordService.updateKeyword(userId, id, keywordRequest);
            return ResponseEntity.ok(ApiResponse.success("키워드가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("키워드 수정에 실패했습니다: " + e.getMessage(), 500));
        }
    }

    @DeleteMapping("/myKeyword/{id}")
    public ResponseEntity<ApiResponse<String>> deleteKeyword(@PathVariable Long id,
                                                             HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요한 기능입니다", 401));
        }

        try {
            keywordService.deleteKeyword(userId, id);
            return ResponseEntity.ok(ApiResponse.success("키워드가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("키워드 삭제에 실패했습니다: " + e.getMessage(), 500));
        }
    }
}

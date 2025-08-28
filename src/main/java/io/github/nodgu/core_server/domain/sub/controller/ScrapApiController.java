package io.github.nodgu.core_server.domain.sub.controller;

import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.sub.dto.ScrapRequest;
import io.github.nodgu.core_server.domain.sub.dto.ScrapListResponse;
import io.github.nodgu.core_server.domain.sub.service.ScrapService;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import io.github.nodgu.core_server.global.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sub")
public class ScrapApiController {

    private final ScrapService scrapService;

    @GetMapping("/myScrap")
    public ResponseEntity<ApiResponse<List<ScrapListResponse>>> findAllScraps(@CurrentUser User user) {
        List<ScrapListResponse> scraps = scrapService.findAllScraps(user)
                .stream()
                .map(ScrapListResponse::new)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("스크랩 조회 성공", scraps));
    }

    @PostMapping("/myScrap")
    public ResponseEntity<ApiResponse<Scrap>> addScrap(@RequestBody ScrapRequest request, @CurrentUser User user) {
        Scrap savedScrap = scrapService.addScrap(request, user);

        // 생성된 스크랩 정보를 응답 객체에 담아 전송 (HTTP 상태 코드 201 Created)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("스크랩 추가 성공", savedScrap));
    }

    @DeleteMapping("/myScrap/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMyScrap(@PathVariable("id") Long id, User user) {

        scrapService.deleteScrap(id, user);

        // 삭제 성공 시 응답 본문 없이 상태 코드 200 OK 반환
        return ResponseEntity.ok(ApiResponse.success("스크랩 삭제 성공", null));
    }
}
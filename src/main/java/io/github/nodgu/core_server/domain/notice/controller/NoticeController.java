package io.github.nodgu.core_server.domain.notice.controller;

import io.github.nodgu.core_server.domain.notice.dto.NoticeRequest;
import io.github.nodgu.core_server.domain.notice.dto.NoticeListResponse;
import io.github.nodgu.core_server.domain.notice.dto.NoticeDetailResponse;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.service.NoticeService;
import io.github.nodgu.core_server.global.dto.ApiResponse;
import io.github.nodgu.core_server.global.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtUtil jwtUtil;

    @Autowired
    public NoticeController(NoticeService noticeService, JwtUtil jwtUtil) {
        this.noticeService = noticeService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/notice")
    public ResponseEntity<ApiResponse<NoticeListResponse>> getNoticeList(
            @RequestParam(name = "notitype", defaultValue = "all") String notitype,
            @RequestParam(name = "page", defaultValue = "1") String page) {
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("페이지 번호는 숫자만 가능합니다", 400));
        }

        NoticeListResponse noticeList = noticeService.getNoticeList(notitype, pageNum);
        return ResponseEntity.ok(ApiResponse.success(noticeList));
    }

    @GetMapping("/notice/{id}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(@PathVariable("id") Long id) {
        if (id == null) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("공지의 id 값이 전달되지 않았습니다", 500));
        }

        NoticeDetailResponse noticeDetail = noticeService.getNoticeDetail(id);
        return ResponseEntity.ok(ApiResponse.success(noticeDetail));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<NoticeListResponse>> getSearchNotice(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "notitype", defaultValue = "all") String notitype,
            @RequestParam(name = "page", defaultValue = "1") String page) {

        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("페이지 번호는 숫자만 가능합니다", 400));
        }

        List<String> includeKeywords = new ArrayList<>();
        List<String> excludeKeywords = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            String[] tokens = query.trim().split("\\s+");
            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()) continue;

                if (token.startsWith("-") && token.length() > 1) {
                    excludeKeywords.add(token.substring(1));
                } else if (token.startsWith("+") && token.length() > 1) {
                    includeKeywords.add(token.substring(1));
                } else {
                    includeKeywords.add(token);
                }
            }
        }

        NoticeListResponse result = noticeService.getSearchNoticeList(
                includeKeywords, excludeKeywords, notitype, pageNum
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/notice")
    public ResponseEntity<ApiResponse<String>> createNotice(@RequestBody NoticeRequest noticeRequest) {
        try {
            noticeService.createNotice(noticeRequest);
            return ResponseEntity.ok(ApiResponse.success("공지사항이 성공적으로 생성되었습니다"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(ApiResponse.error("공지사항이 이미 존재합니다: " + e.getMessage(), 409));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("공지사항 생성에 실패했습니다: " + e.getMessage(), 500));
        }
    }

    @PatchMapping("/notice/{id}")
    public ResponseEntity<ApiResponse<String>> updateNotice(@PathVariable("id") Long id, @RequestBody NoticeRequest noticeRequest) {
        noticeService.updateNotice(id, noticeRequest);
        return ResponseEntity.ok(ApiResponse.success("공지사항이 성공적으로 수정되었습니다"));
    }

    @GetMapping("/notice/noOcrData")
    public ResponseEntity<ApiResponse<List<Notice>>> getNoticeNoOcrData() {
        List<Notice> noticeList = noticeService.getNoticeNoOcrData();
        return ResponseEntity.ok(ApiResponse.success(noticeList));
    }

    @PatchMapping("/notice/addOcrData/{id}")
    public ResponseEntity<ApiResponse<String>> addOcrData(@PathVariable("id") Long id, @RequestBody String ocrData) {
        noticeService.addOcrData(id, ocrData);
        return ResponseEntity.ok(ApiResponse.success("공지사항의 ocr 데이터가 성공적으로 추가되었습니다"));
    }
}
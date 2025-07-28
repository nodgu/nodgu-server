package io.github.nodgu.core_server.domain.notice.controller;

import io.github.nodgu.core_server.domain.notice.dto.NoticeRequest;
import io.github.nodgu.core_server.domain.notice.dto.NoticeListResponse;
import io.github.nodgu.core_server.domain.notice.dto.NoticeDetailResponse;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.service.NoticeService;
import io.github.nodgu.core_server.global.dto.ApiResponse;
import io.github.nodgu.core_server.global.util.JwtUtil;
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
    public ResponseEntity<ApiResponse<NoticeListResponse>> getSearchNotice (@RequestParam(name = "query") String query,
		                                                                    @RequestParam(name = "notitype", defaultValue = "all") String notitype,
    	                                                                    @RequestParam(name = "page", defaultValue = "1") String page) {
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("페이지 번호는 숫자만 가능합니다", 400));
        }

        NoticeListResponse searchNoticeList = noticeService.getSearchNoticeList(query, notitype, pageNum);
        return ResponseEntity.ok(ApiResponse.success(searchNoticeList));
    }

    @PostMapping("/notice")
    public ResponseEntity<ApiResponse<String>> createNotice (@RequestBody NoticeRequest noticeRequest) {
        try {
            noticeService.createNotice(noticeRequest);
            return ResponseEntity.ok(ApiResponse.success("공지사항이 성공적으로 생성되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("공지사항 생성에 실패했습니다" + e.getMessage(), 500));
        }
    }

}
package io.github.nodgu.core_server.domain.notice.service;

import io.github.nodgu.core_server.domain.notice.dto.NoticeListResponse;
import io.github.nodgu.core_server.domain.notice.dto.NoticeDetailResponse;
import io.github.nodgu.core_server.domain.notice.dto.NoticeRequest;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public void createNotice(NoticeRequest request) {
        String tdindex = request.getTitle() + request.getDescription();

        boolean exists = noticeRepository.existsByUnivCodeAndOrgCodeAndSubCodeAndTdindex(
                request.getUnivCode(),
                request.getOrgCode(),
                request.getSubCode(),
                tdindex);

        if (exists) {
            throw new IllegalStateException("이미 동일한 공지사항이 존재합니다.");
        }

        Notice notice = new Notice(
                request.getNoticeId(),
                request.getTitle(),
                request.getUrl(),
                request.getDescription(),
                request.getNotitype(),
                request.getDate(),
                tdindex,
                request.getImgs(),
                request.getLinks(),
                request.getAttachments(),
                request.getOcrData(),
                request.getUnivCode(),
                request.getOrgCode(),
                request.getSubCode());

        noticeRepository.save(notice);
    }

    public NoticeDetailResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다"));
        return NoticeDetailResponse.from(notice);
    }

    public NoticeListResponse getNoticeList(String[] notitypes, int pageNum) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "date"));

        Page<Notice> noticePage;
        if (notitypes.length == 0 || "all".equalsIgnoreCase(notitypes[0])) {
            noticePage = noticeRepository.findAll(pageable);
        } else {
            noticePage = noticeRepository.findByNotitypes(notitypes, pageable);
        }

        List<Notice> notices = noticePage.getContent();
        long totalCount = noticePage.getTotalElements();

        return NoticeListResponse.from(notices, totalCount, pageNum);
    }

    public NoticeListResponse getSearchNoticeList(String query, String[] notitypes, int pageNum) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        // 공백 기준 토큰화, 연속 공백 제거
        List<String> rawTokens = Arrays.stream(query == null ? new String[] {} : query.trim().split("\\s+"))
                .filter(token -> token != null && !token.isBlank())
                .collect(Collectors.toList());

        List<String> includeTokens = new ArrayList<>();
        List<String> excludeTokens = new ArrayList<>();

        for (String token : rawTokens) {
            if (token.startsWith("-") && token.length() > 1) {
                excludeTokens.add(token.substring(1));
            } else {
                includeTokens.add(token);
            }
        }

        Page<Notice> noticePage = noticeRepository.advancedSearch(includeTokens, excludeTokens, notitypes, pageable);

        List<Notice> notices = noticePage.getContent();
        long totalCount = noticePage.getTotalElements();

        return NoticeListResponse.from(notices, totalCount, pageNum);
    }

    public void updateNotice(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다"));
        notice.update(request);
        noticeRepository.save(notice);
    }

    public List<Notice> getNoticeNoOcrData() {
        return noticeRepository.findByOcrDataNullOrEmpty();
    }

    public void addOcrData(Long id, String ocrData) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다"));
        notice.setOcrData(ocrData);
        noticeRepository.save(notice);
    }
}

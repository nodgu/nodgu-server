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

import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public void createNotice(NoticeRequest request) {
        String tdindex = request.getTdindex();

        boolean exists = noticeRepository.existsByUnivCodeAndOrgCodeAndSubCodeAndTdindex(
            request.getUnivCode(),
            request.getOrgCode(),
            request.getSubCode(),
            tdindex
        );

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
            request.getSubCode()
        );

        noticeRepository.save(notice);
    }

    public NoticeDetailResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 공지를 찾을 수 없습니다"));
        return NoticeDetailResponse.from(notice);
    }

    public NoticeListResponse getNoticeList(String notitype, int pageNum) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Notice> noticePage;
        if ("all".equalsIgnoreCase(notitype)) {
            noticePage = noticeRepository.findAll(pageable);
        } else {
            noticePage = noticeRepository.findByNotitype(notitype, pageable);
        }

        List<Notice> notices = noticePage.getContent();
        long totalCount = noticePage.getTotalElements();

        return NoticeListResponse.from(notices, totalCount, pageNum);
    }

    public NoticeListResponse getSearchNoticeList(String query, String notitype, int pageNum) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Notice> noticePage = noticeRepository.searchByQueryAndNotitype(query, notitype, pageable);

        List<Notice> notices = noticePage.getContent();
        long totalCount = noticePage.getTotalElements();

        return NoticeListResponse.from(notices, totalCount, pageNum);
    }
}

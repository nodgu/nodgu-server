package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    // notitype 기준 페이징 조회
    Page<Notice> findByNotitype(String notitype, Pageable pageable);

    // OCR 데이터 null 또는 빈 문자열 조회
    @Query("SELECT n FROM Notice n WHERE n.ocrData IS NULL OR n.ocrData = '' OR n.ocrData = ' ' OR TRIM(n.ocrData) = ''")
    List<Notice> findByOcrDataNullOrEmpty();

    List<Notice> findByOcrDataIsNull();

    Optional<Notice> findByNoticeId(String noticeId);

    boolean existsByUnivCodeAndOrgCodeAndSubCodeAndTdindex(
        String univCode,
        String orgCode,
        String subCode,
        String tdindex
    );
}

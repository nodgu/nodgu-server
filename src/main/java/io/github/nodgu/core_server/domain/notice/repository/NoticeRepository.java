package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByNotitype(String notitype, Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE " +
           "(:notitype = 'all' OR n.notitype = :notitype) AND " +
           "(n.title LIKE %:query% OR n.description LIKE %:query%)")
    Page<Notice> searchByQueryAndNotitype(@Param("query") String query,
                                          @Param("notitype") String notitype,
                                          Pageable pageable);

    Optional<Notice> findByNoticeId(String noticeId);

    boolean existsByUnivCodeAndOrgCodeAndSubCodeAndTdindex(
        String univCode,
        String orgCode,
        String subCode,
        String tdindex
    );

    // OCR 데이터가 null이거나 빈 문자열인 공지사항 조회
    @Query("SELECT n FROM Notice n WHERE n.ocrData IS NULL OR n.ocrData = '' OR n.ocrData = ' ' OR TRIM(n.ocrData) = ''")
    List<Notice> findByOcrDataNullOrEmpty();

    // 기존 메서드 (하위 호환성을 위해 유지)
    List<Notice> findByOcrDataIsNull();
}


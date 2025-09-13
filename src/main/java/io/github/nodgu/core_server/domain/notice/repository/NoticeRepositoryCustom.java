package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeRepositoryCustom {
    Page<Notice> searchByKeywordsAndNotitype(
            List<String> includeKeywords,
            List<String> excludeKeywords,
            String notitype,
            Pageable pageable
    );
}

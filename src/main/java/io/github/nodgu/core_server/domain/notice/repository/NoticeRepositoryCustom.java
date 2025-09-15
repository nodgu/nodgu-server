package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> advancedSearch(
            List<String> includeTokens,
            List<String> excludeTokens,
            String[] notitypes,
            Pageable pageable);
}

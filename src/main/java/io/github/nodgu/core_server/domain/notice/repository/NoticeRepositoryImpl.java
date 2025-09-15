package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Repository
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Notice> advancedSearch(
            List<String> includeTokens,
            List<String> excludeTokens,
            String[] notitypes,
            Pageable pageable) {

        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT n FROM Notice n WHERE 1=1 ");

        boolean filterByTypes = notitypes != null && notitypes.length > 0
                && !(notitypes.length == 1 && "all".equalsIgnoreCase(notitypes[0]));
        if (filterByTypes) {
            jpql.append(" AND n.notitype IN :notitypes");
        }

        // 포함 토큰: 모두 AND
        if (!CollectionUtils.isEmpty(includeTokens)) {
            for (int i = 0; i < includeTokens.size(); i++) {
                jpql.append(" AND (n.title LIKE :inc" + i + " OR n.description LIKE :inc" + i + ")");
            }
        }

        // 제외 토큰: 모두 NOT LIKE
        if (!CollectionUtils.isEmpty(excludeTokens)) {
            for (int i = 0; i < excludeTokens.size(); i++) {
                jpql.append(" AND (n.title NOT LIKE :exc" + i + " AND n.description NOT LIKE :exc" + i + ")");
            }
        }

        // 정렬: 최신순 날짜 기준
        jpql.append(" ORDER BY n.date DESC");

        TypedQuery<Notice> query = entityManager.createQuery(jpql.toString(), Notice.class);
        if (filterByTypes) {
            query.setParameter("notitypes", Arrays.asList(notitypes));
        }
        if (!CollectionUtils.isEmpty(includeTokens)) {
            for (int i = 0; i < includeTokens.size(); i++) {
                query.setParameter("inc" + i, likePattern(includeTokens.get(i)));
            }
        }
        if (!CollectionUtils.isEmpty(excludeTokens)) {
            for (int i = 0; i < excludeTokens.size(); i++) {
                query.setParameter("exc" + i, likePattern(excludeTokens.get(i)));
            }
        }

        // 페이지네이션
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Notice> results = query.getResultList();

        // count 쿼리 동일 바인딩
        String countJpql = jpql.toString().replaceFirst("SELECT n FROM Notice n", "SELECT COUNT(n) FROM Notice n")
                .replaceFirst(" ORDER BY n.date DESC", "");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        if (filterByTypes) {
            countQuery.setParameter("notitypes", Arrays.asList(notitypes));
        }
        if (!CollectionUtils.isEmpty(includeTokens)) {
            for (int i = 0; i < includeTokens.size(); i++) {
                countQuery.setParameter("inc" + i, likePattern(includeTokens.get(i)));
            }
        }
        if (!CollectionUtils.isEmpty(excludeTokens)) {
            for (int i = 0; i < excludeTokens.size(); i++) {
                countQuery.setParameter("exc" + i, likePattern(excludeTokens.get(i)));
            }
        }
        long total = countQuery.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    private String likePattern(String token) {
        if (!StringUtils.hasText(token)) {
            return "%";
        }
        return "%" + token + "%";
    }
}

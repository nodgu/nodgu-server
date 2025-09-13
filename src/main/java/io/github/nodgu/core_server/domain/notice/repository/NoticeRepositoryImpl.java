package io.github.nodgu.core_server.domain.notice.repository;

import io.github.nodgu.core_server.domain.notice.entity.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Notice> searchByKeywordsAndNotitype(
            List<String> includeKeywords,
            List<String> excludeKeywords,
            String notitype,
            Pageable pageable) {

        StringBuilder jpql = new StringBuilder("SELECT n FROM Notice n WHERE 1=1");

        if (!"all".equalsIgnoreCase(notitype)) {
            jpql.append(" AND n.notitype = :notitype");
        }

        // 포함 키워드 AND 조건
        for (int i = 0; i < includeKeywords.size(); i++) {
            jpql.append(" AND (n.title LIKE :inc").append(i)
                .append(" OR n.description LIKE :inc").append(i).append(")");
        }

        // 제외 키워드 AND 조건
        for (int i = 0; i < excludeKeywords.size(); i++) {
            jpql.append(" AND (n.title NOT LIKE :exc").append(i)
                .append(" AND n.description NOT LIKE :exc").append(i).append(")");
        }

        // 조회 쿼리
        TypedQuery<Notice> query = em.createQuery(jpql.toString(), Notice.class);

        if (!"all".equalsIgnoreCase(notitype)) {
            query.setParameter("notitype", notitype);
        }
        for (int i = 0; i < includeKeywords.size(); i++) {
            query.setParameter("inc" + i, "%" + includeKeywords.get(i) + "%");
        }
        for (int i = 0; i < excludeKeywords.size(); i++) {
            query.setParameter("exc" + i, "%" + excludeKeywords.get(i) + "%");
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Notice> results = query.getResultList();

        // 카운트 쿼리
        String countJpql = jpql.toString().replaceFirst("SELECT n", "SELECT COUNT(n)");
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        if (!"all".equalsIgnoreCase(notitype)) {
            countQuery.setParameter("notitype", notitype);
        }
        for (int i = 0; i < includeKeywords.size(); i++) {
            countQuery.setParameter("inc" + i, "%" + includeKeywords.get(i) + "%");
        }
        for (int i = 0; i < excludeKeywords.size(); i++) {
            countQuery.setParameter("exc" + i, "%" + excludeKeywords.get(i) + "%");
        }

        long total = countQuery.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}

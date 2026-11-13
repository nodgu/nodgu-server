package io.github.nodgu.core_server.domain.sub.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByUser_Id(Long userId);

    boolean existsByUser_IdAndTitle(Long userId, String title);

    long countByUser_Id(Long userId);

    // 공지-키워드 연결 테이블은 Notice 쪽이 소유하므로 네이티브 쿼리로 먼저 정리
    @Modifying
    @Query(value = "DELETE FROM notice_keyword WHERE keyword_id IN (SELECT id FROM keyword WHERE user_id = :userId)", nativeQuery = true)
    int deleteNoticeLinksByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Keyword k where k.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

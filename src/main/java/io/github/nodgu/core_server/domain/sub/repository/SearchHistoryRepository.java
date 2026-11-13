package io.github.nodgu.core_server.domain.sub.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import io.github.nodgu.core_server.domain.sub.entity.SearchHistory;
import io.github.nodgu.core_server.domain.user.entity.User;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Modifying
    @Query("delete from SearchHistory e where e.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

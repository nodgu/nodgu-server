package io.github.nodgu.core_server.domain.sub.repository;

import io.github.nodgu.core_server.domain.sub.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByUser_Id(Long userId);
    boolean existsByUser_IdAndTitle(Long userId, String title);
}
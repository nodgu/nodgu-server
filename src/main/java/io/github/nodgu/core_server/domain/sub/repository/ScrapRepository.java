package io.github.nodgu.core_server.domain.sub.repository;

import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // User가 스크랩한 모든 Scrap 엔티티 조회
    List<Scrap> findByUser(User user);
}
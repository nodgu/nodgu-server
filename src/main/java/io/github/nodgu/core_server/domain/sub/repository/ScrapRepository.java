package io.github.nodgu.core_server.domain.sub.repository;

import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // User가 스크랩한 모든 Scrap 엔티티 조회
    List<Scrap> findByUser(User user);

    Optional<Scrap> findByUserAndNotice(User user, Notice notice);
}
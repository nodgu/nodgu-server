package io.github.nodgu.core_server.domain.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.nodgu.core_server.domain.user.entity.PasswordResetToken;
import io.github.nodgu.core_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    void deleteByToken(String token);

    @Modifying
    @Query("delete from PasswordResetToken e where e.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

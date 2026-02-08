package io.github.nodgu.core_server.domain.user.repository;

import io.github.nodgu.core_server.domain.user.entity.PasswordResetToken;
import io.github.nodgu.core_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    void deleteByToken(String token);
}

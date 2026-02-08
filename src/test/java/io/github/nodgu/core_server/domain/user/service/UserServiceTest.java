package io.github.nodgu.core_server.domain.user.service;

import io.github.nodgu.core_server.domain.user.entity.PasswordResetToken;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import io.github.nodgu.core_server.domain.user.repository.PasswordResetTokenRepository;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import io.github.nodgu.core_server.global.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Set default expiration to 24 hours (86400000 ms)
        ReflectionTestUtils.setField(userService, "passwordResetExpirationMs", 86400000L);
    }

    @Test
    @DisplayName("비밀번호 재설정 요청 시 토큰 생성 및 이메일 전송 성공")
    void requestPasswordReset_Success() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        userService.requestPasswordReset(email);

        // Then
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), any(String.class));
    }

    @Test
    @DisplayName("비밀번호 재설정 시 유효한 토큰일 경우 비밀번호 변경 성공")
    void resetPassword_Success() {
        // Given
        String token = "valid-token";
        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // When
        userService.resetPassword(token, newPassword);

        // Then
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(resetToken);
    }
}

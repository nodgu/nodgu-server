package io.github.nodgu.core_server.domain.user.service;

import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DeviceRepository deviceRepository;
    private final io.github.nodgu.core_server.domain.user.repository.PasswordResetTokenRepository passwordResetTokenRepository;
    private final io.github.nodgu.core_server.global.service.EmailService emailService;

    @org.springframework.beans.factory.annotation.Value("${app.auth.password-reset.expiration-ms:86400000}")
    private long passwordResetExpirationMs;

    // 같은 계정으로 재설정 메일을 다시 보낼 수 있을 때까지의 최소 간격 (메일 폭탄 방지)
    @org.springframework.beans.factory.annotation.Value("${app.auth.password-reset.resend-cooldown-seconds:300}")
    private long passwordResetResendCooldownSeconds;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            DeviceRepository deviceRepository,
            io.github.nodgu.core_server.domain.user.repository.PasswordResetTokenRepository passwordResetTokenRepository,
            io.github.nodgu.core_server.global.service.EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.deviceRepository = deviceRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public User createUser(String email, String password, String nickname) {
        long userCount = userRepository.count();
        if (userCount >= 100) {
            throw new IllegalStateException("회원은 최대 100명까지 등록할 수 있습니다");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setIsActive(true);
        user.setIsSuperuser(false);
        user.setIsStaff(false);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User updateUser(User user, String nickname, String password) {
        boolean changed = false;
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
            changed = true;
        }
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
            changed = true;
        }
        if (changed) {
            return userRepository.save(user);
        }
        return user;
    }

    public Device registerDevice(User user, String fcmToken) {
        // 한 기기에서 다른 계정으로 로그인했다면 이전 계정과의 연결을 끊는다
        deviceRepository.deleteByFcmTokenAndOtherUser(fcmToken, user.getId());
        // 같은 기기를 다시 등록하면 중복 행을 만들지 않는다 (중복 푸시 방지)
        return deviceRepository.findAllByUser(user).stream()
                .filter(d -> fcmToken.equals(d.getFcmToken()))
                .findFirst()
                .orElseGet(() -> deviceRepository.save(new Device(user, fcmToken)));
    }

    public void unregisterDevice(User user, String fcmToken) {
        deviceRepository.deleteByUserAndFcmToken(user, fcmToken);
    }

    /**
     * 비밀번호 재설정 메일을 보낸다.
     * 가입 여부를 드러내지 않도록 없는 이메일·이용 정지 계정·재요청 대기 중인 경우에는 아무것도 하지 않고 조용히 끝낸다.
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            return;
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        io.github.nodgu.core_server.domain.user.entity.PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUser(user)
                .orElse(new io.github.nodgu.core_server.domain.user.entity.PasswordResetToken());

        if (resetToken.getExpiryDate() != null) {
            // 발급 시각 = 만료 시각 - 유효 기간
            java.time.LocalDateTime issuedAt = resetToken.getExpiryDate()
                    .minus(java.time.Duration.ofMillis(passwordResetExpirationMs));
            if (issuedAt.isAfter(now.minusSeconds(passwordResetResendCooldownSeconds))) {
                return; // 직전에 보낸 메일의 토큰이 아직 유효하므로 다시 보내지 않는다
            }
        }

        String token = java.util.UUID.randomUUID().toString();

        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(now.plus(java.time.Duration.ofMillis(passwordResetExpirationMs)));

        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) {
        io.github.nodgu.core_server.domain.user.entity.PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("만료된 토큰입니다");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        // 이 시각 이전에 발급된 액세스·리프레시 토큰은 모두 무효가 된다
        user.setPasswordChangedAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
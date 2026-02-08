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
        Device device = new Device(user, fcmToken);
        return deviceRepository.save(device);
    }

    public void unregisterDevice(User user, String fcmToken) {
        deviceRepository.deleteByUserAndFcmToken(user, fcmToken);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        String token = java.util.UUID.randomUUID().toString();

        io.github.nodgu.core_server.domain.user.entity.PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUser(user)
                .orElse(new io.github.nodgu.core_server.domain.user.entity.PasswordResetToken());

        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(java.time.LocalDateTime.now().plusNanos(passwordResetExpirationMs * 1000000L));

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
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.deviceRepository = deviceRepository;
    }
    
    public User createUser(String email, String password, String nickname) {
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
} 
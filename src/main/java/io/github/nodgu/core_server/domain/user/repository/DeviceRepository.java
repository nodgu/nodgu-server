package io.github.nodgu.core_server.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    void deleteByUserAndFcmToken(User user, String fcmToken);

    Optional<Device> findByUser(User user);
}

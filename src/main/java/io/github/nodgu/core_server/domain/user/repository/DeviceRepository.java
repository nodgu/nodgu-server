package io.github.nodgu.core_server.domain.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    void deleteByUserAndFcmToken(User user, String fcmToken);

    Optional<Device> findByUser(User user);

    @Modifying
    @Query("delete from Device e where e.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

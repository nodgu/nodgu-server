package io.github.nodgu.core_server.domain.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    void deleteByUserAndFcmToken(User user, String fcmToken);

    List<Device> findAllByUser(User user);

    boolean existsByUserAndFcmToken(User user, String fcmToken);

    // 같은 기기(FCM 토큰)가 다른 계정에 남아 있으면 그 계정 알림이 이 기기로 가므로 정리할 때 쓴다
    @Modifying
    @Query("delete from Device d where d.fcmToken = :fcmToken and d.user.id <> :userId")
    int deleteByFcmTokenAndOtherUser(@Param("fcmToken") String fcmToken, @Param("userId") Long userId);

    @Modifying
    @Query("delete from Device e where e.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

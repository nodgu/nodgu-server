package io.github.nodgu.core_server.domain.notification.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.nodgu.core_server.domain.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nodgu.core_server.domain.user.entity.User;
import java.util.List;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    List<NotificationSetting> findByUser(User user);

    @Modifying
    @Query("delete from NotificationSetting e where e.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

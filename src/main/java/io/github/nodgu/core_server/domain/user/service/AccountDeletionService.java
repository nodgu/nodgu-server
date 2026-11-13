package io.github.nodgu.core_server.domain.user.service;

import io.github.nodgu.core_server.domain.notification.repository.NotificationRepository;
import io.github.nodgu.core_server.domain.notification.repository.NotificationSettingRepository;
import io.github.nodgu.core_server.domain.sub.repository.KeywordRepository;
import io.github.nodgu.core_server.domain.sub.repository.ScrapRepository;
import io.github.nodgu.core_server.domain.sub.repository.SearchHistoryRepository;
import io.github.nodgu.core_server.domain.user.repository.DeviceRepository;
import io.github.nodgu.core_server.domain.user.repository.PasswordResetTokenRepository;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원탈퇴 시 사용자와 사용자에게 딸린 데이터를 모두 실제로 삭제한다.
 * DB의 ON DELETE CASCADE에 기대지 않고 자식 테이블부터 순서대로 지운다.
 * (리프레시 토큰은 JWT라 DB에 저장되지 않는다. 사용자 행이 지워지면 인증 필터에서 거부된다.)
 */
@Service
public class AccountDeletionService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ScrapRepository scrapRepository;
    private final KeywordRepository keywordRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public AccountDeletionService(UserRepository userRepository,
            DeviceRepository deviceRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            ScrapRepository scrapRepository,
            KeywordRepository keywordRepository,
            SearchHistoryRepository searchHistoryRepository,
            NotificationRepository notificationRepository,
            NotificationSettingRepository notificationSettingRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.scrapRepository = scrapRepository;
        this.keywordRepository = keywordRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
    }

    @Transactional
    public void deleteAccount(Long userId) {
        // 알림 → 알림 설정 → 공지-키워드 연결 → 키워드 순서 (FK 의존 순서)
        notificationRepository.deleteAllByUserId(userId);
        notificationSettingRepository.deleteAllByUserId(userId);
        keywordRepository.deleteNoticeLinksByUserId(userId);
        keywordRepository.deleteAllByUserId(userId);

        scrapRepository.deleteAllByUserId(userId);
        searchHistoryRepository.deleteAllByUserId(userId);
        deviceRepository.deleteAllByUserId(userId);
        passwordResetTokenRepository.deleteAllByUserId(userId);

        userRepository.deleteByIdDirectly(userId);
    }
}

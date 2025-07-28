package io.github.nodgu.core_server.domain.sub.service;

import io.github.nodgu.core_server.domain.sub.dto.ScrapRequest;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.sub.repository.ScrapRepository;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    @Transactional // data 변경 시 transaction 내에서 이루어져야 해서 필요한 annotation
    public Scrap addScrap(Long user_id, ScrapRequest request) {
        // 현재 로그인한 User 엔티티 조회
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user_id));

        // 스크랩하려는 Notice 엔티티 조회
        Notice notice = noticeRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. ID: " + request.getNoticeId()));

        // Scrap 엔티티 생성 (@Builder 사용)
        Scrap newScrap = Scrap.builder()
                .user(user)
                .notice(notice)
                .build();

        // 데이터베이스에 저장
        return scrapRepository.save(newScrap);
    }

    public List<Scrap> findAllScrapsByUser(Long user_id) {
        // 사용자 조회 (사용자가 유효한지 확인)
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + user_id));

        // 해당 사용자가 스크랩한 모든 Scrap 엔티티를 조회
        // ScrapRepository에 findByUser(User user) 메서드가 필요
        return scrapRepository.findByUser(user);
    }

    @Transactional
    public void deleteScrap(Long scrap_id, Long user_id) {
        // 삭제하려는 Scrap 엔티티 조회
        Scrap scrap = scrapRepository.findById(scrap_id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 스크랩을 찾을 수 없습니다. ID: " + scrap_id));

        // 권한 확인: 스크랩의 소유자와 현재 삭제를 요청한 사용자가 동일한지 확인
        if (!scrap.getUser().getId().equals(user_id)) { 
            throw new IllegalArgumentException("이 스크랩을 삭제할 권한이 없습니다. (사용자 ID 불일치)");
        }

        // 스크랩 삭제
        scrapRepository.deleteById(scrap_id);
    }
}
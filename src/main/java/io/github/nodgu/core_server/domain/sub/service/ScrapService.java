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
    public Scrap addScrap(ScrapRequest request, User user) {
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

    public List<Scrap> findAllScraps(User user) {
        // 해당 사용자가 스크랩한 모든 Scrap 엔티티를 조회
        // ScrapRepository에 findByUser(User user) 메서드가 필요
        return scrapRepository.findByUser(user);
    }

    @Transactional
    public void deleteScrap(Long id, User user) {
        // 삭제하려는 Scrap 엔티티 조회
        Scrap scrap = scrapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 스크랩을 찾을 수 없습니다. ID: " + id));

        // 스크랩 삭제
        scrapRepository.deleteById(id);
    }

    public boolean isScrap(Long noticeId, User user) throws Exception {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. ID: " + noticeId));
        return scrapRepository.findByUserAndNotice(user, notice).isPresent();
    }
}
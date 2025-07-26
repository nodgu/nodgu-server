import lombok.RequiredArgsConstructor;
import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import io.github.nodgu.core_server.domain.sub.dto.ScrapRequest;
import io.github.nodgu.core_server.domain.sub.dto.ScrapListResponse;
import io.github.nodgu.core_server.domain.sub.service.ScrapService;
import io.github.nodgu.core_server.domain.user.entity.User;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sub")
public class ScrapApiController {

    private final ScrapService scrapService;

    @GetMapping("myScrap")
    // @AuthenticationPrincipal User user: Spring Security가 현재 로그인한 User 객체를 주입해줍니다.
    // user.getId()를 호출하여 사용자의 ID를 서비스 계층으로 넘깁니다.
    public ResponseEntity<List<ScrapListResponse>> findAllMyScraps(@AuthenticationPrincipal User user) {
        List<ScrapListResponse> scraps = scrapService.findAllScrapsByUser(user.getId())
                .stream()
                .map(ScrapListResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(scraps);
    }

    @PostMapping("/myScrap")
    public ResponseEntity<Scrap> addScrap(@RequestBody ScrapRequest request, @AuthenticationPrincipal User user) {
        // ScrapService의 addScrap 메서드 호출 (사용자 ID와 요청 DTO 전달)
        Scrap savedScrap = scrapService.addScrap(user.getId(), request);

        // 생성된 스크랩 정보를 응답 객체에 담아 전송 (HTTP 상태 코드 201 Created)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedScrap);
    }

    @DeleteMapping("/api/v1/sub/myScrap/{id}")
    public ResponseEntity<Void> deleteMyScrap(@PathVariable("id") Long id, @AuthenticationPrincipal User user) { // @PathVariable 변수명 명시
        Long user_id = 1L;

        // ScrapService의 deleteScrap 메서드 호출 (스크랩 ID와 사용자 ID 전달)
        scrapService.deleteScrap(id, user.getId());

        // 삭제 성공 시 응답 본문 없이 상태 코드 200 OK 반환
        return ResponseEntity.ok()
                .build();
    }
}
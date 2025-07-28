package io.github.nodgu.core_server.domain.sub.dto;

import io.github.nodgu.core_server.domain.sub.entity.Scrap;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 추가
@AllArgsConstructor //모든 필드 값을 인수로 받는 생성자 추가
@Getter
@Setter
public class ScrapRequest {
    private Long noticeId;
}

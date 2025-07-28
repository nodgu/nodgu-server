package io.github.nodgu.core_server.domain.sub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchHistoryRequest {
    @NotBlank(message = "검색어는 필수입니다.")
    private String query;
}

package io.github.nodgu.core_server.domain.sub.dto;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeywordRequest {
    // 활성화값만 바꾸더라도 제목 입력은 필수로 보내야 함
    @NotBlank(message = "키워드 제목은 필수입니다.")
    private String title;
    @JsonProperty("is_active")
    private Boolean isActive;

    public KeywordRequest() {}

    public KeywordRequest(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

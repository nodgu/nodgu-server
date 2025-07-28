package io.github.nodgu.core_server.domain.sub.dto;

import jakarta.validation.constraints.NotBlank;

public class KeywordRequest {
    
    @NotBlank(message = "키워드 제목은 필수입니다.")
    private String title;

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
}

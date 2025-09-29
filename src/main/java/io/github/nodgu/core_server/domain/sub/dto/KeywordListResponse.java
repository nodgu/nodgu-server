package io.github.nodgu.core_server.domain.sub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeywordListResponse {

    private Long id;
    private String title;
    @JsonProperty("is_active")
    private Boolean isActive;

    public KeywordListResponse() {}

    public KeywordListResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public KeywordListResponse(Long id, String title, Boolean isActive) {
        this.id = id;
        this.title = title;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

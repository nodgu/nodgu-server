package io.github.nodgu.core_server.domain.sub.dto;

public class KeywordListResponse {

    private Long id;
    private String title;

    public KeywordListResponse() {}

    public KeywordListResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

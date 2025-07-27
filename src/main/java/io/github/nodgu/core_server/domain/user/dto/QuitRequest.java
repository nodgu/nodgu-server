package io.github.nodgu.core_server.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuitRequest {
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    public QuitRequest(String password) {
        this.password = password;
    }

    public QuitRequest() {}
} 
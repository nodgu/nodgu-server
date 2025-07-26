package io.github.nodgu.core_server.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 30, message = "닉네임은 30자 이내여야 합니다.")
    private String nickname;

    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하여야 합니다.")
    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 
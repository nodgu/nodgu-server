package io.github.nodgu.core_server.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordResetConfirmRequest {
    @NotBlank(message = "토큰은 필수입니다")
    private String token;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하여야 합니다")
    private String newPassword;
}

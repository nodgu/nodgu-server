package io.github.nodgu.core_server.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username:noreply@nodgu.com}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("비밀번호 재설정 요청");
        message.setText("비밀번호를 재설정하려면 다음 토큰을 사용하세요:\n" + token + "\n\n(이 토큰은 설정된 시간 동안만 유효합니다.)");

        emailSender.send(message);
    }
}

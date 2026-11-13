package io.github.nodgu.core_server.global.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final String SENDER_NAME = "NODGU";
    private static final String PASSWORD_RESET_TEMPLATE = "templates/mail/password-reset.html";

    private final JavaMailSender emailSender;
    private final String fromEmail;
    private final long passwordResetExpirationMs;
    private final String passwordResetTemplate;

    public EmailService(JavaMailSender emailSender,
            @Value("${spring.mail.username:noreply@nodgu.com}") String fromEmail,
            @Value("${app.auth.password-reset.expiration-ms:86400000}") long passwordResetExpirationMs) {
        this.emailSender = emailSender;
        this.fromEmail = fromEmail;
        this.passwordResetExpirationMs = passwordResetExpirationMs;
        this.passwordResetTemplate = loadTemplate(PASSWORD_RESET_TEMPLATE);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String validity = formatDuration(passwordResetExpirationMs);

        String html = passwordResetTemplate
                .replace("{{token}}", HtmlUtils.htmlEscape(token))
                .replace("{{validity}}", HtmlUtils.htmlEscape(validity));

        // HTML을 표시하지 못하는 메일 앱을 위한 텍스트 본문
        String text = "[NODGU] 비밀번호 재설정\n\n"
                + "앱의 비밀번호 재설정 화면에 아래 토큰을 붙여 넣어 주세요.\n\n"
                + token + "\n\n"
                + "토큰은 " + validity + " 동안 한 번만 사용할 수 있어요.\n"
                + "직접 요청하지 않으셨다면 이 메일은 무시하셔도 돼요. 비밀번호는 바뀌지 않아요.";

        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail, SENDER_NAME);
            helper.setTo(to);
            helper.setSubject("[NODGU] 비밀번호 재설정 안내");
            helper.setText(text, html);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailPreparationException("비밀번호 재설정 메일 작성 실패", e);
        }

        emailSender.send(message);
    }

    /** 86400000 → "24시간", 1800000 → "30분" */
    static String formatDuration(long millis) {
        long minutes = Math.max(1, millis / 60000);
        long hours = minutes / 60;
        long restMinutes = minutes % 60;
        if (hours == 0) {
            return minutes + "분";
        }
        return restMinutes == 0 ? hours + "시간" : hours + "시간 " + restMinutes + "분";
    }

    private static String loadTemplate(String path) {
        try {
            return StreamUtils.copyToString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("메일 템플릿을 읽을 수 없습니다: " + path, e);
        }
    }
}

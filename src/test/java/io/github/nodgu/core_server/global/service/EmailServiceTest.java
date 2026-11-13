package io.github.nodgu.core_server.global.service;

import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    @DisplayName("비밀번호 재설정 메일은 HTML과 텍스트 본문을 함께 담고, 토큰과 유효 기간이 들어간다")
    void sendPasswordResetEmail() throws Exception {
        JavaMailSender sender = mock(JavaMailSender.class);
        when(sender.createMimeMessage()).thenAnswer(i -> new MimeMessage(Session.getInstance(new Properties())));
        EmailService emailService = new EmailService(sender, "sender@example.com", 86400000L);

        String token = "5a8d5ad4-07df-44cc-97db-a017cc07768e";
        emailService.sendPasswordResetEmail("user@example.com", token);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender).send(captor.capture());
        MimeMessage message = captor.getValue();
        message.saveChanges();

        assertEquals("[NODGU] 비밀번호 재설정 안내", message.getSubject());
        InternetAddress from = (InternetAddress) message.getFrom()[0];
        assertEquals("sender@example.com", from.getAddress());
        assertEquals("NODGU", from.getPersonal());
        assertEquals("user@example.com", message.getAllRecipients()[0].toString());

        List<Part> parts = new ArrayList<>();
        collectLeafParts(message, parts);
        String text = parts.stream().filter(p -> isType(p, "text/plain")).map(EmailServiceTest::content).findFirst().orElseThrow();
        String html = parts.stream().filter(p -> isType(p, "text/html")).map(EmailServiceTest::content).findFirst().orElseThrow();

        assertTrue(text.contains(token));
        assertTrue(text.contains("24시간"));
        assertTrue(html.contains(token));
        assertTrue(html.contains("24시간"));
        assertFalse(html.contains("{{"), "치환되지 않은 자리표시자가 남으면 안 된다");
    }

    @Test
    @DisplayName("유효 기간을 사람이 읽는 문구로 바꾼다")
    void formatDuration() {
        assertEquals("24시간", EmailService.formatDuration(86400000L));
        assertEquals("30분", EmailService.formatDuration(1800000L));
        assertEquals("1시간 30분", EmailService.formatDuration(5400000L));
    }

    private static void collectLeafParts(Part part, List<Part> out) throws Exception {
        Object content = part.getContent();
        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                collectLeafParts(multipart.getBodyPart(i), out);
            }
        } else {
            out.add(part);
        }
    }

    private static boolean isType(Part p, String type) {
        try { return p.isMimeType(type); } catch (Exception e) { return false; }
    }

    private static String content(Part p) {
        try { return p.getContent().toString(); } catch (Exception e) { throw new RuntimeException(e); }
    }
}

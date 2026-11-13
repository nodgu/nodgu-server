package io.github.nodgu.core_server.global.service;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmPushService {
    
    public String sendPushNotification(String token, String title, String body) {
        return sendPushNotification(token, title, body, Map.of());
    }

    /**
     * data는 앱이 알림을 눌렀을 때 쓸 값이다. 예: {"type": "notice", "notice_id": "123"} → 해당 공지 화면 열기
     */
    public String sendPushNotification(String token, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data == null ? Map.of() : data)
            .build();
            return FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package io.github.nodgu.core_server.global.config;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;

@Service
public class FirebaseInitializer {
    @PostConstruct
    public void init() throws Exception {
        try (InputStream serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json")) {

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}

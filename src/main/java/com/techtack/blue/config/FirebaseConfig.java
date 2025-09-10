package com.techtack.blue.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @EventListener(ContextRefreshedEvent.class)
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource(
                    "blue-a3bf9-firebase-adminsdk-fbsvc-5f39143f54.json"
                ).getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("blue-a3bf9")
                    .build();

                FirebaseApp.initializeApp(options);
                System.out.println("[FIREBASE CONFIG] Firebase Admin SDK initialized successfully");
            } else {
                System.out.println("[FIREBASE CONFIG] Firebase Admin SDK already initialized");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
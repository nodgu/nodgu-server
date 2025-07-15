package io.github.nodgu.core_server.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "fcm_token", length = 500)
    private String fcmToken;
    
    // 기본 생성자
    public Device() {}
    
    // 생성자
    public Device(User user, String fcmToken) {
        this.user = user;
        this.fcmToken = fcmToken;
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getFcmToken() {
        return fcmToken;
    }
    
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
} 
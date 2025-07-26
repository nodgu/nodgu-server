package io.github.nodgu.core_server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
} 
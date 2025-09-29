package io.github.nodgu.core_server.domain.sub.entity;

import io.github.nodgu.core_server.domain.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "KEYWORD",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "title"})) // 사용자별로 중복 금지
public class Keyword {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Keyword() { this.isActive = true; }

    public Keyword(String title) {
        this.title = title;
        this.isActive = true;
    }

    public void setUserId(Long userId) {
        User user = new User();
        user.setId(userId);
        this.user = user;
    }

    // Getter/Setter
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

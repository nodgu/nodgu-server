package io.github.nodgu.core_server.domain.sub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import io.github.nodgu.core_server.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String query;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SearchHistory() {}

    public SearchHistory(User user, String query) {
        this.user = user;
        this.query = query;
        this.createdAt = LocalDateTime.now();
    }
}

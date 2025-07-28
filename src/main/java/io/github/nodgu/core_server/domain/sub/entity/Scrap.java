package io.github.nodgu.core_server.domain.sub.entity;

import jakarta.persistence.*;
import lombok.*;

import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.notice.entity.Notice;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @Table(name = "scraps")
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    public Scrap(Notice notice, User user) {
        this.user = user;
        this.notice = notice;
    }
}
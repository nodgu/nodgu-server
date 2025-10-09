package io.github.nodgu.core_server.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "restaurant_id", updatable = false)
    private Long restaurantId;

    @Column(name = "univ")
    private String univ;

    @Column(name = "campus")
    private String campus;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();

    @Builder
    public Restaurant(Long restaurantId, String univ, String campus, String name) {
        this.restaurantId = restaurantId;
        this.univ = univ;
        this.campus = campus;
        this.name = name;
    }
}

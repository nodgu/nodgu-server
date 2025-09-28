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

    @Column(name = "univ")
    private String univ;

    @Column(name = "campus")
    private String campus;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "activated")
    private String activated;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();

    @Builder
    public Restaurant(String univ, String campus, String name, String address, String activated) {
        this.univ = univ;
        this.campus = campus;
        this.name = name;
        this.address = address;
        this.activated = activated;
    }
}

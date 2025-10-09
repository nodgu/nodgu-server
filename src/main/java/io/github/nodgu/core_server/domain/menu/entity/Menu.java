package io.github.nodgu.core_server.domain.menu.entity;

import io.github.nodgu.core_server.global.converter.StringListJsonConverter;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "corner")
    private String corner;
    
    @Column(name = "activated")
    private String activated;

    @Column(name="food")
    private List<String> food = new ArrayList<>();

    public String getActivated() { return this.activated; }

    @Builder
    public Menu(Restaurant restaurant, String date, String time, String corner, String activated, List<String> food) {
        this.restaurant = restaurant;
        this.date = date;
        this.time = time;
        this.corner = corner;
        this.activated = activated;
        this.food = (food==null)?new ArrayList<>() : new ArrayList<>(food);
    }
}
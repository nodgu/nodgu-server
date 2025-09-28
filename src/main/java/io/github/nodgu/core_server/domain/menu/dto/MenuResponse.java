package io.github.nodgu.core_server.domain.menu.dto;

import io.github.nodgu.core_server.domain.menu.entity.Menu;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MenuResponse {
    private Long id;
    private String time;
    private char division;
    private List<String> food;
    private String univ;
    private String campus;
    private String name;
    private String address;
    private String activated;

    public MenuResponse(Menu request) {
        this.id = request.getId();
        this.time = request.getTime();
        this.division = request.getDivision();
        this.food = request.getFood();
        this.univ = request.getRestaurant().getUniv();
        this.campus = request.getRestaurant().getCampus();
        this.name = request.getRestaurant().getName();
        this.address = request.getRestaurant().getAddress();
        this.activated = request.getRestaurant().getActivated();
    }
}
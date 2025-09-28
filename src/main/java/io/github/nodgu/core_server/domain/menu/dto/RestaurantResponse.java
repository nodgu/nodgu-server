package io.github.nodgu.core_server.domain.menu.dto;

import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantResponse {
    private Long id;
    private String univ;
    private String campus;
    private String name;
    private String address;
    private String activated;

    public RestaurantResponse(Restaurant request) {
        this.id = request.getId();
        this.univ = request.getUniv();
        this.campus = request.getCampus();
        this.name = request.getName();
        this.address = request.getAddress();
        this.activated = request.getActivated();
    }
}

package io.github.nodgu.core_server.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantRequest {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("univ")
    private String univ;

    @JsonProperty("campus")
    private String campus;

    @JsonProperty("name")
    private String name;

}
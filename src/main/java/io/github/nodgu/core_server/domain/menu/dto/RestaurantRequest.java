package io.github.nodgu.core_server.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantIdRequest {
    @JsonProperty("restaurant_id")
    private Long restaurantId;
}
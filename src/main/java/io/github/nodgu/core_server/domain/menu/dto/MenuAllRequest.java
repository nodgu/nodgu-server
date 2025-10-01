package io.github.nodgu.core_server.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MenuRequest {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("restaurant_id")
    private Long restaurantId;
    
    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("division")
    private char division;

    @JsonProperty("food")
    private List<String> food;
}
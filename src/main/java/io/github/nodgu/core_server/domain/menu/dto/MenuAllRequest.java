package io.github.nodgu.core_server.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MenuAllRequest {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("corner")
    private String corner;

    @JsonProperty("activated")
    private String activated;

    @JsonProperty("food")
    private List<String> food;

    public String getActivated() { return this.activated; }
}
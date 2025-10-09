package io.github.nodgu.core_server.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MenuRequest {
    // DB에서 한 번에 가져와서 프론트에서는 식당마다 빠르게 출력하기 위함
    // @JsonProperty("id")
    // private Long id;
    
    @JsonProperty("date")
    private String date;
}
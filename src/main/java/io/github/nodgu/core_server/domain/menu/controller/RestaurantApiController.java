package io.github.nodgu.core_server.domain.menu.controller;

import io.github.nodgu.core_server.domain.menu.dto.MenuRequest;
import io.github.nodgu.core_server.domain.menu.dto.RestaurantResponse;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class RestaurantApiController {
    private final RestaurantService restaurantService;

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantResponse> getRestaurant(@RequestBody MenuRequest request) {
        Restaurant restaurant = restaurantService.getById(request.getRestaurantId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestaurantResponse(restaurant));
    }
}

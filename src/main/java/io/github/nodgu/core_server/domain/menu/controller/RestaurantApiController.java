package io.github.nodgu.core_server.domain.menu.controller;

import io.github.nodgu.core_server.domain.menu.dto.RestaurantRequest;
import io.github.nodgu.core_server.domain.menu.dto.RestaurantResponse;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> getRestaurant(@RequestBody MenuRequest request) {
        Restaurant restaurant = restaurantService.getById(request.getRestaurantId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestaurantResponse(restaurant));
    }
}

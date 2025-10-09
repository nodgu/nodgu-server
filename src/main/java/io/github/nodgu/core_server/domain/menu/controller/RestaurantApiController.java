package io.github.nodgu.core_server.domain.menu.controller;

import io.github.nodgu.core_server.domain.menu.dto.RestaurantRequest;
import io.github.nodgu.core_server.domain.menu.dto.RestaurantResponse;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.service.RestaurantService;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class RestaurantApiController {
    private final RestaurantService restaurantService;

    @PostMapping("/restaurants")
    public ResponseEntity<Void> saveRestaurants(
            @RequestBody List<RestaurantRequest> requests) {
         restaurantService.saveRestaurant(requests);

        return ResponseEntity.status(HttpStatus.CREATED).build(); 
    }
    @GetMapping("/restaurants/ids")
    public ResponseEntity<List<Long>> getRestaurantIds() {
        return ResponseEntity.ok(restaurantService.findAllRestaurantIds());
    }
}

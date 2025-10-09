package io.github.nodgu.core_server.domain.menu.service;

import io.github.nodgu.core_server.domain.menu.dto.RestaurantRequest;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<Long> findAllRestaurantIds() {
        return restaurantRepository.findAllRestaurantIds();
    }

    @Transactional
    public void saveRestaurant(List<RestaurantRequest> requests) {
        for (RestaurantRequest request : requests) {
            restaurantRepository.findByRestaurantId(request.getRestaurantId())
                .orElseGet(() -> restaurantRepository.save(
                new Restaurant(
                    request.getRestaurantId(),
                    request.getUniv(),
                    request.getCampus(),
                    request.getName()
                )
            ));
        }
    }
}

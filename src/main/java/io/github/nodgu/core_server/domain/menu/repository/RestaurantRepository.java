package io.github.nodgu.core_server.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByRestaurantId(Long restaurantId);

    @Query("select r.restaurantId from Restaurant r")
    List<Long> findAllRestaurantIds();
}

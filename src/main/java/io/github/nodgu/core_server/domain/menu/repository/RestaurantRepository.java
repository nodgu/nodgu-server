package io.github.nodgu.core_server.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import io.github.nodgu.core_server.domain.menu.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByUnivAndCampusAndName(String univ, String campus, String name);
}

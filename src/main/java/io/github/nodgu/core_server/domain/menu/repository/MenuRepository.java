package io.github.nodgu.core_server.domain.menu.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import io.github.nodgu.core_server.domain.menu.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("""
        select m from Menu m
        join fetch m.restaurant r
        where r.id = :restaurantId and m.date = :date
    """)
    Optional<Menu> findByRestaurant_IdAndDate(@Param("restaurantId") Long restaurantId, @Param("date") String date);
}
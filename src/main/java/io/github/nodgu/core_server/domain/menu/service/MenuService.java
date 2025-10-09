package io.github.nodgu.core_server.domain.menu.service;

import io.github.nodgu.core_server.domain.menu.dto.MenuAllRequest;
import io.github.nodgu.core_server.domain.menu.dto.MenuRequest;
import io.github.nodgu.core_server.domain.menu.entity.Menu;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.repository.MenuRepository;
import io.github.nodgu.core_server.domain.menu.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    @Transactional(readOnly = true)
    public Menu getMenu(MenuRequest request) {
        return menuRepository
                .findByDate(request.getDate())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 조건의 학식 정보가 없습니다."));
    }

    @Transactional
    public void saveMenu(List<MenuAllRequest> requests) {
        for (MenuAllRequest req : requests) {
                Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "식당 없음"));

                var existing = menuRepository.findByAll(
                req.getRestaurantId(), req.getDate(), req.getTime()
);

                Menu menu = existing.orElseGet(() -> Menu.builder()
                                .restaurant(restaurant)
                                .date(req.getDate())
                                .time(req.getTime())
                                .corner(req.getCorner() != null && !req.getCorner().isEmpty() ? req.getCorner() : null)
                                .activated(req.getActivated())
                                .build());
                menu.setFood(req.getFood());
                menuRepository.save(menu);
        }
    } 
}
package io.github.nodgu.core_server.domain.menu.service;

import io.github.nodgu.core_server.domain.menu.dto.MenuRequest;
import io.github.nodgu.core_server.domain.menu.entity.Menu;
import io.github.nodgu.core_server.domain.menu.entity.Restaurant;
import io.github.nodgu.core_server.domain.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    @Transactional(readOnly = true)
    public Menu getMenu(MenuRequest request) {
        return menuRepository
                .findByRestaurant_IdAndDate(request.getRestaurantId(), request.getDate())
                .orElseThrow(() -> new IllegalArgumentException("해당 조건의 학식 정보가 없습니다."));
    }
}

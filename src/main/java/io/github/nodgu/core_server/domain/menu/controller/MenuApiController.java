package io.github.nodgu.core_server.domain.menu.controller;

import io.github.nodgu.core_server.domain.menu.service.MenuService;
import io.github.nodgu.core_server.domain.menu.dto.MenuRequest;
import io.github.nodgu.core_server.domain.menu.dto.MenuResponse;
import io.github.nodgu.core_server.domain.menu.entity.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuApiController {
    private final MenuService menuService;

    @PostMapping("/menus")
    public ResponseEntity<MenuResponse> getMenu(@RequestBody MenuRequest request) {
        Menu menu = menuService.getMenu(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MenuResponse(menu));
    }
}
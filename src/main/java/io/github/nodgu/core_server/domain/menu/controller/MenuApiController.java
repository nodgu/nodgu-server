package io.github.nodgu.core_server.domain.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponse> getMenu(@RequestBody MenuRequest request) {
        Menu menu = menuService.getMenu(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MenuResponse(menu));
    }
}
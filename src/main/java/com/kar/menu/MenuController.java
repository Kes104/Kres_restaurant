package com.kar.menu;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import java.util.Optional;

@Controller("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Get("/dishes")
    public Iterable<Dish> getAllDishes() {
        return menuService.getAllDishes();
    }

    @Get("/dishes/{id}")
    public Optional<Dish> getDishById(@PathVariable Long id) {
        return menuService.getDishById(id);
    }

    @Get("/categories")
    public Iterable<MenuCategory> getAllCategories() {
        return menuService.getAllCategories();
    }

    @Get("/category/{name}")
    public Optional<MenuCategory> getCategoryByName(@PathVariable String name) {
        return menuService.getCategoryByName(name);
    }

    @Get("/dishes/category/{categoryId}")
    public Iterable<Dish> getDishesByCategoryId(@PathVariable Long categoryId) {
        return menuService.getDishesByCategoryId(categoryId);
    }
}
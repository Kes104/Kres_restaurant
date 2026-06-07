package com.kar.menu;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class MenuService {

    private final DishRepository DishRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuService(DishRepository DishRepository,
                       MenuCategoryRepository menuCategoryRepository) {
        this.DishRepository = DishRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }

    public Iterable<Dish> getAllDishes() {
        return DishRepository.findAll();
    }

    public Optional<Dish> getDishById(Long id) {
        return DishRepository.findById(id);
    }

    public Iterable<Dish> getDishesByCategoryId(Long categoryId) {
        return DishRepository.findByCategoryId(categoryId);
    }

    public Iterable<MenuCategory> getAllCategories() {
        return menuCategoryRepository.findAll();
    }

    public Optional<MenuCategory> getCategoryByName(String name) {
        return menuCategoryRepository.findByName(name);
    }

    public List<Dish> findByName(String name) {
        return DishRepository.findByNameIlike(name);
    }

    public Optional<MenuCategory> getCategoryById(Long id) {
        return menuCategoryRepository.findById(id);
    }
}
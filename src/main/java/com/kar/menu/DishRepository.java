package com.kar.menu;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DishRepository extends CrudRepository<Dish, Long> {
    List<Dish> findByName(String name);
    List<Dish> findByCategoryId(Long categoryId);
    List<Dish> findByNameIlike(String name);
}

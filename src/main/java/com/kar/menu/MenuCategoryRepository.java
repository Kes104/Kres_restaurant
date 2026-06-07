package com.kar.menu;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MenuCategoryRepository extends CrudRepository<MenuCategory, Long> {
    Optional<MenuCategory> findByName(String name);
    Optional<MenuCategory> findById(Long id);
}

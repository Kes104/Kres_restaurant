package com.kar.chef;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ChefRepository extends CrudRepository<Chef, Long> {
    List<Chef> findByMealtype(Chef.Meals mealtype);
}

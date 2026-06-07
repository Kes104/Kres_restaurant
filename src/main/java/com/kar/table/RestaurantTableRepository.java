package com.kar.table;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RestaurantTableRepository extends CrudRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByStatus(RestaurantTable.TableStatus status);
    List<RestaurantTable> findByStatusAndSize(RestaurantTable.TableStatus status, int size);
    List<RestaurantTable> findBySize(int size);
}
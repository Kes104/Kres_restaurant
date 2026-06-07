package com.kar.order;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findBySessionId(Long sessionId);
}

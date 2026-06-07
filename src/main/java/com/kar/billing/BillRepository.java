package com.kar.billing;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BillRepository extends CrudRepository<Bill, Long> {
    List<Bill> findBySessionId(Long sessionId);
}

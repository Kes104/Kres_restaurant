package com.kar.session;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DiningSessionRepository extends CrudRepository<DiningSession, Long> {
    List<DiningSession> findByStatus(DiningSession.stat status);
    List<DiningSession> findByCustomerId(Long customerId);
    List<DiningSession> findByTableId(Long tableId);
}

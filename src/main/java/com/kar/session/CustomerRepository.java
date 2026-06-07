package com.kar.session;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
// import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Query("SELECT * FROM customer WHERE telegram_chat_id = :telegramChatID")
    Optional<Customer> findByTelegramChatID(long telegramChatID);
}

package com.kar.chef;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ChefOrderRepository extends CrudRepository<ChefOrder, Long> {
    List<ChefOrder> findByChefId(Long chefId);
    List<ChefOrder> findByOrderId(Long orderId);
    List<ChefOrder> findByStatus(ChefOrder.sta status);
    List<ChefOrder> findByPriority(ChefOrder.pri priority);
    List<ChefOrder> findByChefIdAndStatus(Long chefId, ChefOrder.sta status);

}

package com.kar.order;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@Serdeable
@MappedEntity("order_item")
public class OrderItem {
    
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    @MappedProperty("order_id") // may need to define the table relationship in the database, let's check.
    private Long orderId;
    @MappedProperty("dish_id") // may need to define the table relationship in the database, let's check.
    private Long dishId;
    @MappedProperty("quantity")
    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}

package com.kar.chef;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.MappedProperty;

@Serdeable
@MappedEntity("chef_order_item")
public class ChefOrderItem {
    
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    private Long chefOrderId;
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

    public Long getChefOrderId() {
        return chefOrderId;
    }

    public void setChefOrderId(Long chefOrderId) {
        this.chefOrderId = chefOrderId;
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

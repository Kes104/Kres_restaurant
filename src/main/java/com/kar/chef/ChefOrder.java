package com.kar.chef;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;

import java.time.LocalDateTime;

@Serdeable
@MappedEntity("chef_order")
public class ChefOrder {
    
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    @MappedProperty("chef_id") // may need to define the table relationship in the database, let's check.
    private Long chefId;



    @MappedProperty("order_id") // may need to define the table relationship in the database, let's check.
    private Long orderId;

    private sta status;
    private pri priority;

    @MappedProperty("created_at")
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum sta {
        PENDING, IN_PROGRESS, COMPLETED
    }

    public enum pri {
        LOW, MEDIUM, HIGH
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChefId() {
        return chefId;
    }

    public void setChefId(Long chefId) {
        this.chefId = chefId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public sta getStatus() {
        return status;
    }

    public void setStatus(sta status) {
        this.status = status;
    }

    public pri getPriority() {
        return priority;
    }

    public void setPriority(pri priority) {
        this.priority = priority;
    }

}

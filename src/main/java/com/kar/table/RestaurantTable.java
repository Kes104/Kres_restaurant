package com.kar.table;

import io.micronaut.data.annotation.*;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("restaurant_table")
public class RestaurantTable {

    // fields - id, size, status (available, occupied, reserved)
    
    @Id
    @GeneratedValue(GeneratedValue.Type.SEQUENCE)
    private Long id;

    private int size;

    private TableStatus status;

    public enum TableStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
package com.kar.chef;

import io.micronaut.data.annotation.*;
import io.micronaut.serde.annotation.*;
import java.time.LocalTime;
import io.micronaut.data.annotation.MappedProperty;


@Serdeable
@MappedEntity("chef")
public class Chef {

    // fields - id, name, mealtype, activeFrom, activeTill
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    private String name;
    private Meals mealtype;

    public enum Meals {
        BREAKFAST,
        LUNCH,
        SNACKS
    }

    @MappedProperty("active_from")
    private LocalTime activeFrom;
    @MappedProperty("active_till")
    private LocalTime activeTill;

    @MappedProperty("chef_status")
    private String chefStatus;

    @MappedProperty("current_order_remaining")
    private Integer currentOrderRemaining;

    public String getChefStatus() { return chefStatus; }
    public void setChefStatus(String chefStatus) { this.chefStatus = chefStatus; }

    public Integer getCurrentOrderRemaining() { return currentOrderRemaining; }
    public void setCurrentOrderRemaining(Integer currentOrderRemaining) {
        this.currentOrderRemaining = currentOrderRemaining;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meals getMealtype() {
        return mealtype;
    }

    public void setMealtype(Meals mealtype) {
        this.mealtype = mealtype;
    }

    public LocalTime getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalTime activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalTime getActiveTill() {
        return activeTill;
    }

    public void setActiveTill(LocalTime activeTill) {
        this.activeTill = activeTill;
    }
}
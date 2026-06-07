package com.kar.menu;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.annotation.MappedProperty;

@Serdeable
@MappedEntity("menu_category")
public class MenuCategory {

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    private String name;
    @MappedProperty("chef_id") 
    private Long chefId;

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

    public Long getChefId() {
        return chefId;
    }

    public void setChefId(Long chefId) {
        this.chefId = chefId;
    }    
}

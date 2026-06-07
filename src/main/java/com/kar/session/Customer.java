package com.kar.session;

import io.micronaut.data.annotation.*;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("customer")
public class Customer {
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    @MappedProperty("telegram_chat_id")
    private Long telegramChatID;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramChatID() {
        return telegramChatID;
    }

    public void setTelegramChatID(Long telegramChatID) {
        this.telegramChatID = telegramChatID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

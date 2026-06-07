package com.kar.session;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SessionStartRequest {
    private Long telegramChatID;
    private String name;

    public Long getTelegramChatID() { return telegramChatID; }
    public void setTelegramChatID(Long telegramChatID) { this.telegramChatID = telegramChatID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
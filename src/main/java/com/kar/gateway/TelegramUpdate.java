package com.kar.gateway;

import io.micronaut.serde.annotation.Serdeable;
import com.fasterxml.jackson.annotation.JsonProperty;

@Serdeable
public class TelegramUpdate {

    @JsonProperty("update_id")
    private Long updateId;

    @JsonProperty("message")
    private TelegramMessage message;

    public Long getUpdateId() { return updateId; }
    public void setUpdateId(Long updateId) { this.updateId = updateId; }

    public TelegramMessage getMessage() { return message; }
    public void setMessage(TelegramMessage message) { this.message = message; }
}
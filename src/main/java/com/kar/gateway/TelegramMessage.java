package com.kar.gateway;

import io.micronaut.serde.annotation.Serdeable;
import com.fasterxml.jackson.annotation.JsonProperty;

@Serdeable
public class TelegramMessage {

    @JsonProperty("message_id")
    private Long messageId;

    @JsonProperty("from")
    private TelegramUser from;

    @JsonProperty("text")
    private String text;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public TelegramUser getFrom() { return from; }
    public void setFrom(TelegramUser from) { this.from = from; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
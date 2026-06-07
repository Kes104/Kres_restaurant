package com.kar.order;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.annotation.*;
import java.time.LocalDateTime;

@Serdeable
@MappedEntity("orders")
public class Order {

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;

    @MappedProperty("session_id")
    private Long sessionId;

    @MappedProperty("created_at")
    private LocalDateTime createdAt;

    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
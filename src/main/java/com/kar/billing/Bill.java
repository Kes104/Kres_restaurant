package com.kar.billing;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Serdeable
@MappedEntity("bill")
public class Bill {
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;

    @MappedProperty("session_id")
    private long sessionId;
    @MappedProperty("total_amount")
    private double totalAmount;
    @MappedProperty("tax_amount")
    private double taxAmount;
    @MappedProperty("final_amount")
    private double finalAmount;
    @MappedProperty("status")
    private String status;
    @MappedProperty("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getSessionId() {
        return sessionId;
    }
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public double getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }
    public double getFinalAmount() {
        return finalAmount;
    }
    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

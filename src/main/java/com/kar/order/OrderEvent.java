package com.kar.order;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;


@Serdeable
public class OrderEvent {
    private Long orderId;
    private Long chefId;
    private Long sessionId;
    private String priority;
    private List<OrderItemRequest> items;

    private Integer prepTime;
    public Integer getPrepTime() { return prepTime; }
    public void setPrepTime(Integer prepTime) { this.prepTime = prepTime; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId;}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}

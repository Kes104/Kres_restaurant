package com.kar.billing;

import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.annotation.Queue;
import com.kar.order.OrderEvent;

@RabbitListener
public class BillingQueueConsumer {

    private final BillingService billingService;

    public BillingQueueConsumer(BillingService billingService) {
        this.billingService = billingService;
    }

    @Queue("billing-queue")
    public void receiveOrderEvent(OrderEvent event) {
        System.out.println("=== BILLING EVENT RECEIVED FOR SESSION: " + event.getSessionId() + " ===");
        if (event.getSessionId() != null) {
            billingService.generateBill(event.getSessionId());
        }
    }
}
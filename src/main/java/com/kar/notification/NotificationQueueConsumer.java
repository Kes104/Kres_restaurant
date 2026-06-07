package com.kar.notification;

import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.annotation.Queue;
import com.kar.order.OrderEvent;

@RabbitListener
public class NotificationQueueConsumer {

    @Queue("notification-queue")
    public void receiveOrderEvent(OrderEvent event) {
        if (event.getPriority().equals("REJECTED")) {
            System.out.println("NOTIFY CUSTOMER: Your order was rejected — chef not available at this time.");
        } else if (event.getPriority().equals("READY")) {
            System.out.println("NOTIFY CUSTOMER: All your dishes are ready! Session: " + event.getSessionId());
        } else {
            System.out.println("NOTIFY CUSTOMER: Order " + event.getOrderId() + " is being prepared.");
        }
    }
}
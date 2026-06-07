package com.kar.order;

import io.micronaut.rabbitmq.annotation.RabbitClient;
import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.Mandatory;
// import io.micronaut.rabbitmq.annotation.RabbitProperty;
import reactor.core.publisher.Mono;


@RabbitClient("order-exchange")

public interface OrderEventProducer {

    @Binding("chef1-queue")
    @Mandatory
    Mono<Void> sendToChef1(OrderEvent event);

    @Binding("chef2-queue")
    @Mandatory
    Mono<Void> sendToChef2(OrderEvent event);

    @Binding("chef3-queue")
    @Mandatory
    Mono<Void> sendToChef3(OrderEvent event);

    @Binding("billing-queue")
    @Mandatory
    Mono<Void> sendToBilling(OrderEvent event);

    @Binding("notification-queue")
    @Mandatory
    Mono<Void> sendToNotification(OrderEvent event);
}
package com.kar;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.micronaut.context.annotation.Context;
import io.micronaut.rabbitmq.connect.ChannelPool;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

@Singleton
public class RabbitMQConfiguration {

    private final ChannelPool channelPool;

    public RabbitMQConfiguration(ChannelPool channelPool) {
        this.channelPool = channelPool;
    }

    @PostConstruct
    public void setupQueuesAndExchanges() throws Exception {
        Channel channel = channelPool.getChannel();
        try {
            // Declare exchange
            channel.exchangeDeclare("order-exchange", "direct", true);

            // Declare queues
            channel.queueDeclare("chef1-queue", true, false, false, null);
            channel.queueDeclare("chef2-queue", true, false, false, null);
            channel.queueDeclare("chef3-queue", true, false, false, null);
            channel.queueDeclare("billing-queue", true, false, false, null);
            channel.queueDeclare("notification-queue", true, false, false, null);

            // Bind queues to exchange
            channel.queueBind("chef1-queue", "order-exchange", "chef1-queue");
            channel.queueBind("chef2-queue", "order-exchange", "chef2-queue");
            channel.queueBind("chef3-queue", "order-exchange", "chef3-queue");
            channel.queueBind("billing-queue", "order-exchange", "billing-queue");
            channel.queueBind("notification-queue", "order-exchange", "notification-queue");

            System.out.println("=== RabbitMQ queues and exchange declared successfully ===");
        } finally {
            channelPool.returnChannel(channel);
        }
    }
}

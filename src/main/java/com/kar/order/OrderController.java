package com.kar.order;

import io.micronaut.http.annotation.*;

@Controller("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Post
    public Iterable<Order> createOrder(@Body Long sessionId) {
        return orderService.createOrder(sessionId);
    }

    @Get("/session/{sessionId}")
    public Iterable<Order> getOrdersBySessionId(@PathVariable Long sessionId) {
        return orderService.getOrdersBySessionId(sessionId);
    }

    @Post("/{orderId}/items")
    public Iterable<OrderItem> addItemToOrder(@PathVariable Long orderId,
                                               @Body OrderItemRequest request) {
        return orderService.addItemtoOrder(orderId, request.getDishId(), request.getQuantity());
    }

    @Get("/{orderId}/items")
    public Iterable<OrderItem> getItemsByOrderId(@PathVariable Long orderId) {
        return orderService.getItemsByOrderId(orderId);
    }
}
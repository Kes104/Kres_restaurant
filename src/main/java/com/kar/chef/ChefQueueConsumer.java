package com.kar.chef;

import com.kar.menu.DishRepository;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import com.kar.order.OrderEvent;
import com.kar.order.OrderEventProducer;

@RabbitListener
public class ChefQueueConsumer {

    private final ChefOrderRepository chefOrderRepository;
    private final OrderEventProducer orderEventProducer;
    private final DishRepository dishRepository;
    private final ChefService chefService;

    public ChefQueueConsumer(ChefOrderRepository chefOrderRepository,
                              OrderEventProducer orderEventProducer,
                             DishRepository dishRepository,
                             ChefService chefService) {
        this.chefOrderRepository = chefOrderRepository;
        this.orderEventProducer = orderEventProducer;
        this.dishRepository = dishRepository;
        this.chefService = chefService;
    }

    @Queue("chef1-queue")
    public void receiveChef1(OrderEvent event) {
        try {
            System.out.println("=== CHEF 1 RECEIVED ORDER ID: " + event.getOrderId() + " ===");
            chefService.markChefBusy(event.getChefId(), event.getPrepTime());
            processAndSync(event);
        } catch (Exception e) {
            System.out.println("Chef 1 error processing order: " + e.getMessage());
        }
    }

    @Queue("chef2-queue")
    public void receiveChef2(OrderEvent event) {
        try {
            System.out.println("=== CHEF 2 RECEIVED ORDER ID: " + event.getOrderId() + " ===");
            chefService.markChefBusy(event.getChefId(), event.getPrepTime());
            processAndSync(event);
        } catch (Exception e) {
            System.out.println("Chef 2 error processing order: " + e.getMessage());
        }
    }

    @Queue("chef3-queue")
    public void receiveChef3(OrderEvent event) {
        try {
            System.out.println("=== CHEF 3 RECEIVED ORDER ID: " + event.getOrderId() + " ===");
            chefService.markChefBusy(event.getChefId(), event.getPrepTime());
            processAndSync(event);
        } catch (Exception e) {
            System.out.println("Chef 3 error processing order: " + e.getMessage());
        }
    }

    private void processAndSync(OrderEvent event) {
        var chefOrders = chefOrderRepository.findByOrderId(event.getOrderId());
        boolean allComplete = true;
        for (var chefOrder : chefOrders) {
            if (chefOrder.getStatus() != ChefOrder.sta.COMPLETED){
                allComplete = false;
                break;
            }
        }
        if (allComplete) {
            // Reduce chef time when order is done
            var dishOpt = dishRepository.findById(event.getItems().getFirst().getDishId());
            dishOpt.ifPresent(dish ->
                    chefService.reduceChefTime(event.getChefId(),
                            dish.getPrepTime() * event.getItems().getFirst().getQuantity()));

            System.out.println("=== ALL CHEFS DONE FOR ORDER: " +
                    event.getOrderId() + " — NOTIFYING CUSTOMER ===");
            event.setPriority("READY");
            Thread.ofVirtual().start(() ->
                    orderEventProducer.sendToNotification(event).subscribe());
        }
    }
}
package com.kar.order;

import com.kar.chef.ChefOrder;
import com.kar.chef.ChefOrderRepository;
import jakarta.inject.Singleton;
import com.kar.menu.DishRepository;
import com.kar.menu.MenuCategoryRepository;
import com.kar.chef.ChefRepository;
import com.kar.chef.ChefService;

import java.time.LocalDateTime;

@Singleton
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventProducer orderEventProducer;
    private final DishRepository dishRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final ChefRepository chefRepository;
    private final ChefService chefService;
    private final ChefOrderRepository chefOrderRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        DishRepository dishRepository,
                        MenuCategoryRepository menuCategoryRepository,
                        ChefRepository chefRepository,
                        ChefService chefService,
                        OrderEventProducer orderEventProducer,
                        ChefOrderRepository chefOrderRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.chefRepository = chefRepository;
        this.chefService = chefService;
        this.orderEventProducer = orderEventProducer;
        this.chefOrderRepository = chefOrderRepository;
    }

    public Iterable<Order> createOrder(Long sessionId) {
    Order newOrder = new Order();
    newOrder.setSessionId(sessionId);
    newOrder.setCreatedAt(LocalDateTime.now());
    newOrder.setStatus("PENDING");
    orderRepository.save(newOrder);
    return orderRepository.findBySessionId(sessionId);
    }

    public Iterable<Order> getOrdersBySessionId(Long sessionId) {
        return orderRepository.findBySessionId(sessionId);
    }

    public Iterable<OrderItem> addItemtoOrder(Long orderId, Long dishId, int quantity) {
        OrderItem newItem = new OrderItem();
        newItem.setOrderId(orderId);
        newItem.setDishId(dishId);
        newItem.setQuantity(quantity);
        orderItemRepository.save(newItem);

        publishOrderToChef(orderId, dishId, quantity);
        
        return orderItemRepository.findByOrderId(orderId);
    }

    public Iterable<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    private void publishOrderToChef(Long orderId, Long dishId, int quantity) {
        var dish = dishRepository.findById(dishId).orElse(null);
        if (dish == null) return;

        var category = menuCategoryRepository.findById(dish.getCategoryId()).orElse(null);
        if (category == null) return;

        var chef = chefRepository.findById(category.getChefId()).orElse(null);
        if (chef == null) return;

        if (!chefService.isChefAvailable(chef)) {
            OrderEvent rejectionEvent = new OrderEvent();
            rejectionEvent.setOrderId(orderId);
            rejectionEvent.setPriority("REJECTED");
            orderEventProducer.sendToNotification(rejectionEvent).subscribe();
            return;
        }

        OrderEvent event = new OrderEvent();
        event.setOrderId(orderId);
        event.setChefId(chef.getId());
        event.setSessionId(null);
        event.setPriority("NORMAL");

        OrderItemRequest item = new OrderItemRequest();
        item.setDishId(dishId);
        item.setQuantity(quantity);
        event.setItems(java.util.List.of(item));

        // Save ChefOrder record
        ChefOrder chefOrder = new ChefOrder();
        chefOrder.setOrderId(orderId);
        chefOrder.setChefId(chef.getId());
        chefOrder.setStatus(ChefOrder.sta.IN_PROGRESS);
        chefOrder.setPriority(ChefOrder.pri.MEDIUM);
        chefOrder.setCreatedAt(java.time.LocalDateTime.now());
        chefOrderRepository.save(chefOrder);

        int totalPrepTime = 0;
        var dishForTime = dishRepository.findById(dishId).orElse(null);
        if (dishForTime != null && dishForTime.getPrepTime() != null) {
            totalPrepTime = dishForTime.getPrepTime() * quantity;
        }
        event.setPrepTime(totalPrepTime);

        switch (chef.getMealtype()) {
            case BREAKFAST -> {
                orderEventProducer.sendToChef1(event).subscribe();
                chefService.markChefBusy(chef.getId(), totalPrepTime);
            }
            case LUNCH -> {
                orderEventProducer.sendToChef2(event).subscribe();
                chefService.markChefBusy(chef.getId(), totalPrepTime);
            }
            case SNACKS -> {
                orderEventProducer.sendToChef3(event).subscribe();
                chefService.markChefBusy(chef.getId(), totalPrepTime);
            }
        }
        orderEventProducer.sendToNotification(event);

        Thread.ofVirtual().start(() -> publishOrderToChef(orderId, dishId, quantity));

    }
}
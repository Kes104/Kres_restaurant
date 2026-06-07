package com.kar.chef;

import java.time.LocalTime;

import jakarta.inject.Singleton;

@Singleton
public class ChefService {
    
    private final ChefRepository chefRepository;
    private final ChefOrderRepository chefOrderRepository;
    private final ChefOrderItemRepository chefOrderItemRepository;

    public ChefService(ChefRepository chefRepository, 
                       ChefOrderRepository chefOrderRepository,
                       ChefOrderItemRepository chefOrderItemRepository) {
        this.chefRepository = chefRepository;
        this.chefOrderRepository = chefOrderRepository;
        this.chefOrderItemRepository = chefOrderItemRepository;
    }

    public Iterable<Chef> getChefsByMealType(Chef.Meals mealtype) {
        return chefRepository.findByMealtype(mealtype);
    }

    public Iterable<ChefOrder> getAvailableChefs(Long chefId) {
        return chefOrderRepository.findByChefIdAndStatus(chefId, ChefOrder.sta.IN_PROGRESS);
    }

    public Iterable<ChefOrderItem> createChefOrderItem(Long chefOrderId, Long dishId, int quantity) {
        ChefOrderItem newItem = new ChefOrderItem();
        newItem.setChefOrderId(chefOrderId);
        newItem.setDishId(dishId);
        newItem.setQuantity(quantity);
        chefOrderItemRepository.save(newItem);
        return chefOrderItemRepository.findByChefOrderId(chefOrderId);
    }

    public void updateChefOrderStatus(Long chefOrderId, ChefOrder.sta status) {
        ChefOrder order = chefOrderRepository.findById(chefOrderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            chefOrderRepository.update(order);   
        }
    }

    public boolean isChefAvailable(Chef chef) {
        LocalTime now = LocalTime.now();
        LocalTime from = chef.getActiveFrom();
        LocalTime till = chef.getActiveTill();
        return !now.isBefore(from) && !now.isAfter(till);
    }

    public int getEstimatedWaitTime(Long chefId, int dishPrepTime) {
        var chefOpt = chefRepository.findById(chefId);
        if (chefOpt.isEmpty()) return dishPrepTime;
        var chef = chefOpt.get();
        int remaining = chef.getCurrentOrderRemaining() == null ?
                0 : chef.getCurrentOrderRemaining();
        return remaining + dishPrepTime;
    }

    public void markChefBusy(Long chefId, int prepTime) {
        var chefOpt = chefRepository.findById(chefId);
        chefOpt.ifPresent(chef -> {
            chef.setChefStatus("BUSY");
            int current = chef.getCurrentOrderRemaining() == null ?
                    0 : chef.getCurrentOrderRemaining();
            // Cap at reasonable maximum — 120 minutes
            int newTime = Math.min(current + prepTime, 120);
            chef.setCurrentOrderRemaining(newTime);
            chefRepository.update(chef);
        });
    }

    public void reduceChefTime(Long chefId, int prepTime) {
        var chefOpt = chefRepository.findById(chefId);
        chefOpt.ifPresent(chef -> {
            int remaining = chef.getCurrentOrderRemaining() == null ?
                    0 : chef.getCurrentOrderRemaining();
            remaining = Math.max(0, remaining - prepTime);
            chef.setCurrentOrderRemaining(remaining);
            if (remaining == 0) chef.setChefStatus("FREE");
            chefRepository.update(chef);
        });
    }
}

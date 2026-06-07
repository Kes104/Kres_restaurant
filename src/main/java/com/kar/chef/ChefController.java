package com.kar.chef;

import io.micronaut.http.annotation.*;

@Controller("/chef")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    @Get("/mealtype/{mealtype}")
    public Iterable<Chef> getChefsByMealType(@PathVariable Chef.Meals mealtype) {
        return chefService.getChefsByMealType(mealtype);
    }

    @Get("/orders/{chefId}")
    public Iterable<ChefOrder> getAvailableOrders(@PathVariable Long chefId) {
        return chefService.getAvailableChefs(chefId);
    }

    @Put("/orders/{chefOrderId}/status")
    public void updateChefOrderStatus(@PathVariable Long chefOrderId,
                                      @Body ChefOrder.sta status) {
        chefService.updateChefOrderStatus(chefOrderId, status);
    }
}
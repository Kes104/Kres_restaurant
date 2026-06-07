package com.kar.billing;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import java.util.List;

@Controller("/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @Post("/generate/{sessionId}")
    public Bill generateBill(@PathVariable Long sessionId) {
        return billingService.generateBill(sessionId);
    }

    @Get("/session/{sessionId}")
    public List<Bill> getBillsBySession(@PathVariable Long sessionId) {
        return billingService.getBillsBySessionId(sessionId);
    }
}
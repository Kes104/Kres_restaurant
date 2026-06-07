package com.kar.billing;

import jakarta.inject.Singleton;
import com.kar.order.OrderRepository;
import com.kar.order.OrderItemRepository;
import com.kar.menu.DishRepository;
import com.kar.session.DiningSessionRepository;
import com.kar.session.DiningSession;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class BillingService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final DiningSessionRepository diningSessionRepository;

    public BillingService(BillRepository billRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          DishRepository dishRepository,
                          DiningSessionRepository diningSessionRepository) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
        this.diningSessionRepository = diningSessionRepository;
    }

    public Bill generateBill(Long sessionId) {
        var orders = orderRepository.findBySessionId(sessionId);

        double total = 0.0;
        for (var order : orders) {
            var items = orderItemRepository.findByOrderId(order.getId());
            for (var item : items) {
                var dish = dishRepository.findById(item.getDishId()).orElse(null);
                if (dish != null) {
                    total += dish.getPrice() * item.getQuantity();
                }
            }
        }

        double tax = total * 0.05;
        double finalAmount = total + tax;

        Bill bill = new Bill();
        bill.setSessionId(sessionId);
        bill.setTotalAmount(total);
        bill.setTaxAmount(tax);
        bill.setFinalAmount(finalAmount);
        bill.setStatus("GENERATED");
        bill.setCreatedAt(LocalDateTime.now());
        billRepository.save(bill);

        var sessionOpt = diningSessionRepository.findById(sessionId);
        sessionOpt.ifPresent(session -> {
            session.setStatus(DiningSession.stat.CLOSED);
            diningSessionRepository.update(session);
        });

        return bill;
    }

    public List<Bill> getBillsBySessionId(Long sessionId) {
        return billRepository.findBySessionId(sessionId);
    }
}
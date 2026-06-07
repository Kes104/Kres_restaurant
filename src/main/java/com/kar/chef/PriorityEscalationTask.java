package com.kar.chef;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import com.kar.session.DiningSessionRepository;
import com.kar.session.DiningSession;
import java.time.LocalDateTime;

@Singleton
public class PriorityEscalationTask {

    private final DiningSessionRepository diningSessionRepository;
    private final ChefOrderRepository chefOrderRepository;

    public PriorityEscalationTask(DiningSessionRepository diningSessionRepository,
                                   ChefOrderRepository chefOrderRepository) {
        this.diningSessionRepository = diningSessionRepository;
        this.chefOrderRepository = chefOrderRepository;
    }

    @Scheduled(fixedDelay = "5m")
    public void escalatePriorities() {
        var sessions = diningSessionRepository.findByStatus(DiningSession.stat.ORDERING);
        for (var session : sessions) {
            if (session.getStartTime() == null) continue;
            boolean waitingTooLong = session.getStartTime()
                    .isBefore(LocalDateTime.now().minusMinutes(15));
            if (waitingTooLong) {
                var chefOrders = chefOrderRepository.findByOrderId(session.getId());
                for (var chefOrder : chefOrders) {
                    chefOrder.setPriority(ChefOrder.pri.HIGH);
                    chefOrderRepository.update(chefOrder);
                }
            }
        }
    }
}
package com.kar.session;

import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Singleton
public class SessionService {
    private final DiningSessionRepository diningSessionRepository;
    private final CustomerRepository customerRepository;

    public SessionService(DiningSessionRepository diningSessionRepository,
                          CustomerRepository customerRepository) {
        this.diningSessionRepository = diningSessionRepository;
        this.customerRepository = customerRepository;
    }

    public Iterable<DiningSession> findOrCreateCustomerSession(long telegramChatID, String name) {
        var customerOpt = customerRepository.findByTelegramChatID(telegramChatID);
        if (customerOpt.isEmpty()) {
            var newCustomer = new Customer();
            newCustomer.setTelegramChatID(telegramChatID);
            newCustomer.setName(name);
            customerOpt = Optional.of(customerRepository.save(newCustomer));
        }
        var customer = customerOpt.get();
        var sessions = diningSessionRepository.findByCustomerId(customer.getId());

        // Filter only active sessions — ignore CLOSED ones
        var activeSessions = new java.util.ArrayList<DiningSession>();
        for (var session : sessions) {
            if (session.getStatus() != DiningSession.stat.CLOSED) {
                activeSessions.add(session);
            }
        }

        if (activeSessions.isEmpty()) {
            var newSession = new DiningSession();
            newSession.setCustomerId(customer.getId());
            newSession.setStatus(DiningSession.stat.WAITING);
            newSession.setStartTime(LocalDateTime.now());
            diningSessionRepository.save(newSession);
            return List.of(newSession);
        }
        return activeSessions;
    }

    public Iterable<DiningSession> getSessionByCustomerId(Long customerId) {
        return diningSessionRepository.findByCustomerId(customerId);
    }

    public Iterable<DiningSession> createSession(Long customerId, Long tableId) {
        var newSession = new DiningSession();
        newSession.setCustomerId(customerId);
        newSession.setTableId(tableId);
        diningSessionRepository.save(newSession);
        return diningSessionRepository.findByCustomerId(customerId);
    }

    public Iterable<DiningSession> findSessionByCustomerId(Long customerId) {
        return diningSessionRepository.findByCustomerId(customerId);
    }

    public Iterable<DiningSession> updateSessionStatus(Long sessionId, DiningSession.stat status) {
        var sessionOpt = diningSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            var session = sessionOpt.get();
            session.setStatus(status);
            diningSessionRepository.update(session);
            return diningSessionRepository.findByCustomerId(session.getCustomerId());
        }
        return List.of();
    }

    public void assignTableToSession(Long sessionId, Long tableId) {
        var sessionOpt = diningSessionRepository.findById(sessionId);
        sessionOpt.ifPresent(session -> {
            session.setTableId(tableId);
            session.setStatus(DiningSession.stat.SEATED);
            diningSessionRepository.update(session);
        });
    }

    public Optional<DiningSession> getActiveSessionByCustomerId(Long customerId) {
        var sessions = diningSessionRepository.findByCustomerId(customerId);
        for (var session : sessions) {
            if (session.getStatus() == null ||
                    session.getStatus() != DiningSession.stat.CLOSED) {
                return Optional.of(session);
            }
        }
        return Optional.empty();
    }

    public Optional<DiningSession> getActiveSessionByTelegramId(Long telegramChatId) {
        var customerOpt = customerRepository.findByTelegramChatID(telegramChatId);
        if (customerOpt.isEmpty()) return Optional.empty();
        return getActiveSessionByCustomerId(customerOpt.get().getId());
    }
}

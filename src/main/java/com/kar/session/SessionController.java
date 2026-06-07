package com.kar.session;

import io.micronaut.http.annotation.*;

@Controller("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Post("/start")
    public Iterable<DiningSession> findOrCreateSession(@Body SessionStartRequest request) {
        return sessionService.findOrCreateCustomerSession(request.getTelegramChatID(), request.getName());
    }

    @Get("/customer/{customerId}")
    public Iterable<DiningSession> getSessionByCustomerId(@PathVariable Long customerId) {
        return sessionService.getSessionByCustomerId(customerId);
    }

    @Put("/{sessionId}/status")
    public void updateSessionStatus(@PathVariable Long sessionId,
                                    @Body DiningSession.stat status) {
        sessionService.updateSessionStatus(sessionId, status);
    }
}
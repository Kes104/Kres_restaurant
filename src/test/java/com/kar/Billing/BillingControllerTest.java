package com.kar.Billing;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class BillingControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testGetBillsBySession() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/billing/session/1"));
        assertNotNull(response);
    }
}
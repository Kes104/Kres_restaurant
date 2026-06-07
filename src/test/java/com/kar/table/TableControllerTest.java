package com.kar.table;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.HttpRequest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class TableControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testGetAllTablesNotEmpty() {
        String response = client.toBlocking()
                                .retrieve(HttpRequest.GET("/tables"));
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetAvailableTables() {
        String response = client.toBlocking()
                                .retrieve(HttpRequest.GET("/tables/available"));
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
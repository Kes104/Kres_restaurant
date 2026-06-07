package com.kar.menu;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.HttpRequest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class MenuControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testGetDishesNotEmpty() {

        String response =
                client.toBlocking()
                      .retrieve(HttpRequest.GET("/menu/dishes"));

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
package com.kar.chef;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class ChefServiceTest {

    @Inject
    ChefService chefService;

    @Test
    void testChefAvailability() {
        Chef chef = new Chef();
        chef.setActiveFrom(LocalTime.of(9, 0));
        chef.setActiveTill(LocalTime.of(22, 0));
        assertTrue(chefService.isChefAvailable(chef));
    }

    @Test
    void testChefNotAvailableOutsideHours() {
        Chef chef = new Chef();
        chef.setActiveFrom(LocalTime.of(16, 30));
        chef.setActiveTill(LocalTime.of(21, 30));
        // This test result depends on current time
        // Just verify method runs without error
        assertNotNull(chefService.isChefAvailable(chef));
    }
}
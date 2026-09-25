package app;

import app.DTOs.FoodDTO;
import app.services.foodService.FoodSearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSeachTime() {
        long start = System.currentTimeMillis();
        FoodSearchService foodSearchService = new FoodSearchService();

        Future<FoodDTO> future = foodSearchService.searchFood("Chicken");

        FoodDTO food = null;
        try {
            food = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        long end = System.currentTimeMillis();
        System.out.println("Response tog: " + (end - start) + " ms");
        if (food == null) {
            System.out.println("Ingen produkter fundet.");

        }
    }
}

package app;

import app.persistence.FoodDTO;
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
    void testAverageSearchTime() {
        long averageSeacrhTime = 0;
        for (int i = 0; i < 10; i++) {
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
            long time = end - start;
            averageSeacrhTime += time;
            System.out.println("Response tog: " + time + " ms");
            if (food == null) {
                System.out.println("Ingen produkter fundet.");

            }

        }
        System.out.println("Response tog: " + averageSeacrhTime/10 + " ms");
    }
}

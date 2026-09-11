package app.services.foodService;

import app.persistence.FoodDTO;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FoodSearchService {

    private final FoodAPI foodAPI;
    private final ExecutorService executor;

    public FoodSearchService() {
        this.foodAPI = new FoodAPI();
        this.executor = Executors.newFixedThreadPool(2);
    }

    public Future<FoodDTO> searchFood(String search) {

        Callable<FoodDTO> task = () -> {
            return foodAPI.searchFood(search);
        };

        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
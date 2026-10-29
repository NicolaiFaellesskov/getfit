package app.services.foodService;

import app.DAOs.FoodDAO;
import app.DTOs.FoodDTO;
import app.config.HibernateConfig;
import app.entities.Food;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FoodSearchService {

    private final FoodAPI foodAPI;
    private final FoodDAO foodDAO;
    private final ExecutorService executor;

    public FoodSearchService() {
        this.foodAPI = new FoodAPI();
        this.foodDAO = new FoodDAO(
                HibernateConfig.getEntityManagerFactory()
        );
        this.executor = Executors.newFixedThreadPool(2);
    }

    public Future<List<FoodDTO>> searchFood(String search) {
        Callable<List<FoodDTO>> task = () -> {
            List<FoodDTO> foods = foodAPI.searchFood(search);

            for (FoodDTO dto : foods) {
                if (dto.getBarcode() == null || dto.getBarcode().isBlank()) {
                    continue;
                }

                Food existingFood = foodDAO.findByBarcode(dto.getBarcode());

                if (existingFood == null) {
                    Food food = new Food();
                    food.setName(dto.getName());
                    food.setBrand(dto.getBrand());
                    food.setBarcode(dto.getBarcode());

                    FoodDTO.Nutriments nutriments = dto.getNutriments();

                    if (nutriments != null) {
                        food.setCalories(nutriments.getOrZero(nutriments.getEnergyKcal()));
                        food.setProteinGrams(nutriments.getOrZero(nutriments.getProtein()));
                        food.setFatGrams(nutriments.getOrZero(nutriments.getFat()));
                        food.setCarbohydratesGrams(nutriments.getOrZero(nutriments.getCarbohydrates()));
                        food.setSugarGrams(nutriments.getOrZero(nutriments.getSugars()));
                        food.setFiberGrams(nutriments.getOrZero(nutriments.getFiber()));
                        food.setSaltGrams(nutriments.getOrZero(nutriments.getSalt()));
                        food.setSaturatedFatGrams(nutriments.getOrZero(nutriments.getSaturatedFat()));
                    }

                    foodDAO.save(food);
                }
            }

            return foods;
        };

        return executor.submit(task);
    }
}
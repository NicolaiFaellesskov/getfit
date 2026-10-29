package app.controllers;

import app.DAOs.DailyLogDAO;
import app.DAOs.FoodAmountDAO;
import app.DAOs.FoodDAO;
import app.DAOs.MealDAO;
import app.DTOs.AddFoodRequest;
import app.DTOs.FoodDTO;
import app.DTOs.MealDTO;
import app.config.HibernateConfig;
import app.entities.DailyLog;
import app.entities.Food;
import app.entities.FoodAmount;
import app.entities.Meal;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.persistence.FoodAmountMapper;
import app.persistence.FoodMapper;
import app.persistence.MealMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MealController {

    private final MealDAO mealDAO;
    private final DailyLogDAO dailyLogDAO;
    private final FoodDAO foodDAO;
    private final FoodAmountDAO foodAmountDAO;

    public MealController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        this.mealDAO = new MealDAO(emf);
        this.dailyLogDAO = new DailyLogDAO(emf);
        this.foodDAO = new FoodDAO(emf);
        this.foodAmountDAO = new FoodAmountDAO(emf);
    }

    public void read(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Meal meal;
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        try {
            meal = mealDAO.findById(id);
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }
        if (meal == null) {
            ctx.status(404).result("Meal not found");
            return;
        }

        if (!belongsToUser(meal, userId)) {
            ctx.status(403).result("Access denied");
            return;
        }

        ctx.status(200);
        ctx.json(MealMapper.toDTO(meal));
    }

    public void readAll(Context ctx) {
        Integer userId = ctx.attribute("userId");
        List<MealDTO> meals;

        try {
            meals = mealDAO.findAll()
                    .stream()
                    .filter(meal -> belongsToUser(meal, userId))
                    .map(MealMapper::toDTO)
                    .toList();
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }
        ctx.status(200);
        ctx.json(meals);
    }

    public void create(Context ctx) {
        Integer userId = ctx.attribute("userId");
        MealDTO request = ctx.bodyAsClass(MealDTO.class);
        try {
            DailyLog dailyLog = dailyLogDAO.findById(
                    request.getDailyLogId()
            );

            if (dailyLog.getUser() == null
                    || !dailyLog.getUser().

                    getId().

                    equals(userId)) {
                ctx.status(403).result("Access denied");
                return;
            }

            Meal meal = Meal.builder()
                    .mealName(request.getMealName())
                    .dailyLog(dailyLog)
                    .build();
            dailyLog.addMeal(meal);
            mealDAO.save(meal);

            ctx.status(201);
            ctx.json(MealMapper.toDTO(meal));
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }
    }

    public void update(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Meal meal;

        int id = ctx.pathParamAsClass("id", Integer.class).get();

        try {
            meal = mealDAO.findById(id);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }
        if (meal == null) {
            ctx.status(404).result("Meal not found");
            return;
        }

        if (!belongsToUser(meal, userId)) {
            ctx.status(403).result("Access denied");
            return;
        }

        MealDTO request = ctx.bodyAsClass(MealDTO.class);
        try {
            DailyLog dailyLog = dailyLogDAO.findById(
                    request.getDailyLogId()
            );

            if (dailyLog.getUser() == null
                    || !dailyLog.getUser().getId().equals(userId)) {
                ctx.status(403).result("Access denied");
                return;
            }

            meal.setMealName(request.getMealName());
            meal.setDailyLog(dailyLog);

            mealDAO.update(meal);

            ctx.status(200);
            ctx.json(MealMapper.toDTO(meal));
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }
    }

    public void delete(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Meal meal;

        int id = ctx.pathParamAsClass("id", Integer.class).get();

        try {
            meal = mealDAO.findById(id);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }
        if (meal == null) {
            ctx.status(404).result("Meal not found");
            return;
        }

        if (!belongsToUser(meal, userId)) {
            ctx.status(403).result("Access denied");
            return;
        }

        try {
            mealDAO.delete(id);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }
        ctx.status(204);
    }

    public void addFoodAmount(Context ctx) {
        int mealId = ctx.pathParamAsClass("id", Integer.class).get();
        Meal meal;

        try {
            meal = mealDAO.findById(mealId);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }
        if (meal == null) {
            ctx.status(404).result("Meal not found");
            return;
        }

        Integer userId = ctx.attribute("userId");

        if (!belongsToUser(meal, userId)) {
            ctx.status(403).result("Access denied");
            return;
        }

        AddFoodRequest request = ctx.bodyAsClass(AddFoodRequest.class);

        if (request.getFood() == null || request.getAmount() <= 0) {
            ctx.status(400)
                    .result("Food and a positive amount are required");
            return;
        }

        FoodDTO foodDTO = request.getFood();

        Food food = null;


        if (foodDTO.getBarcode() != null
                && !foodDTO.getBarcode().isBlank()) {
            try {
                food = foodDAO.findByBarcode(foodDTO.getBarcode());
            } catch (DatabaseException e) {
                throw new ApiException(404, e.getMessage());
            }
        }


        if (food == null) {
            food = FoodMapper.toEntity(foodDTO);
            try {
                foodDAO.save(food);
            } catch (DatabaseException e) {
                throw new ApiException(500, e.getMessage());
            }
        }

        // Knyt produktet til måltidet med den angivne mængde
        FoodAmount foodAmount = FoodAmount.builder()
                .food(food)
                .gram(request.getAmount())
                .build();

        meal.addFoodAmount(foodAmount);

        try {
            foodAmountDAO.save(foodAmount);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }

        ctx.status(201);
        ctx.json(FoodAmountMapper.toDTO(foodAmount));
    }

    public void deleteFoodAmount(Context ctx) {
        Integer userId = ctx.attribute("userId");
        int foodAmountId = ctx.pathParamAsClass("foodAmountId", Integer.class).get();

        FoodAmount foodAmount = foodAmountDAO.findById(foodAmountId);

        if (foodAmount == null) {
            ctx.status(404).result("Food amount not found");
            return;
        }

        Meal meal = foodAmount.getMeal();

        if (!belongsToUser(meal, userId)) {
            ctx.status(403).result("Access denied");
            return;
        }

        foodAmountDAO.delete(foodAmountId);
        ctx.status(204);
    }

    private boolean belongsToUser(Meal meal, Integer userId) {
        return meal != null
                && meal.getDailyLog() != null
                && meal.getDailyLog().getUser() != null
                && meal.getDailyLog().getUser().getId().equals(userId);
    }
}
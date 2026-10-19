package app.controllers;

import app.DAOs.DailyLogDAO;
import app.DAOs.FoodAmountDAO;
import app.DAOs.FoodDAO;
import app.DAOs.MealDAO;
import app.DTOs.AddFoodRequest;
import app.DTOs.FoodAmountDTO;
import app.DTOs.FoodDTO;
import app.DTOs.MealDTO;
import app.config.HibernateConfig;
import app.entities.DailyLog;
import app.entities.Food;
import app.entities.FoodAmount;
import app.entities.Meal;
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
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Meal meal = mealDAO.findById(id);

        ctx.status(200);
        ctx.json(MealMapper.toDTO(meal));
    }

    public void readAll(Context ctx) {
        List<MealDTO> meals = mealDAO.findAll()
                .stream()
                .map(MealMapper::toDTO)
                .toList();

        ctx.status(200);
        ctx.json(meals);
    }

    public void create(Context ctx) {
        MealDTO request = ctx.bodyAsClass(MealDTO.class);

        DailyLog dailyLog = dailyLogDAO.findById(
                request.getDailyLogId()
        );

        if (dailyLog == null) {
            ctx.status(404).result("DailyLog not found");
            return;
        }

        Meal meal = Meal.builder()
                .mealName(request.getMealName())
                .dailyLog(dailyLog)
                .build();

        mealDAO.save(meal);

        ctx.status(201);
        ctx.json(MealMapper.toDTO(meal));
    }

    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        MealDTO request = ctx.bodyAsClass(MealDTO.class);

        DailyLog dailyLog = dailyLogDAO.findById(
                request.getDailyLogId()
        );

        if (dailyLog == null) {
            ctx.status(404).result("DailyLog not found");
            return;
        }

        Meal meal = mealDAO.findById(id);

        meal.setMealName(request.getMealName());
        meal.setDailyLog(dailyLog);

        mealDAO.update(meal);

        ctx.status(200);
        ctx.json(MealMapper.toDTO(meal));
    }

    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        mealDAO.delete(id);

        ctx.status(204);
    }

    public void addFood(Context ctx) {
        int mealId = ctx.pathParamAsClass("id", Integer.class).get();

        Meal meal = mealDAO.findById(mealId);

        if (meal == null) {
            ctx.status(404).result("Meal not found");
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

        // Find eksisterende produkt via stregkode
        if (foodDTO.getBarcode() != null
                && !foodDTO.getBarcode().isBlank()) {
            food = foodDAO.findByBarcode(foodDTO.getBarcode());
        }

        // Opret kun produktet, hvis det ikke findes
        if (food == null) {
            food = FoodMapper.toEntity(foodDTO);
            foodDAO.save(food);
        }

        // Knyt produktet til måltidet med den angivne mængde
        FoodAmount foodAmount = FoodAmount.builder()
                .food(food)
                .meal(meal)
                .amount(request.getAmount())
                .build();

        foodAmountDAO.save(foodAmount);

        ctx.status(201);
        ctx.json(FoodAmountMapper.toDTO(foodAmount));
    }

    private boolean validatePrimaryKey(Integer id) {
        return mealDAO.findById(id) != null;
    }
}
package app.controllers;

import app.DAOs.DailyLogDAO;
import app.DAOs.MealDAO;
import app.DTOs.MealDTO;
import app.entities.DailyLog;
import app.entities.Meal;
import io.javalin.Javalin;

import java.util.List;

public class MealController {

    private final MealDAO mealDAO;
    private final DailyLogDAO dailyLogDAO;

    public MealController(MealDAO mealDAO, DailyLogDAO dailyLogDAO) {
        this.mealDAO = mealDAO;
        this.dailyLogDAO = dailyLogDAO;
    }

    public void addRoutes(Javalin app) {

        // GET all Meals
        app.get("/meals", ctx -> {

            try {
                List<MealDTO> meals = mealDAO.findAll()
                        .stream()
                        .map(this::toDTO)
                        .toList();

                ctx.json(meals);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result(e.getMessage());
            }
        });


        // GET Meal by ID
        app.get("/meals/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            Meal meal = mealDAO.findById(id);

            if (meal == null) {
                ctx.status(404).result("Meal not found");
                return;
            }

            ctx.json(toDTO(meal));
        });


        // POST new Meal
        app.post("/meals", ctx -> {

            MealDTO mealDTO = ctx.bodyAsClass(MealDTO.class);

            // Find DailyLog med det ID, som kommer fra JSON
            DailyLog dailyLog = dailyLogDAO.findById(
                    mealDTO.getDailyLogId()
            );

            // Hvis DailyLog 9 ikke findes
            if (dailyLog == null) {
                ctx.status(404).result("DailyLog not found");
                return;
            }

            // Opret Meal og koble den til DailyLog
            Meal meal = Meal.builder()
                    .mealName(mealDTO.getMealName())
                    .dailyLog(dailyLog)
                    .build();

            // Gem Meal
            mealDAO.save(meal);

            ctx.status(201).json(toDTO(meal));
        });



        // PUT existing Meal
        app.put("/meals/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            Meal existingMeal = mealDAO.findById(id);

            if (existingMeal == null) {
                ctx.status(404).result("Meal not found");
                return;
            }

            MealDTO mealDTO = ctx.bodyAsClass(MealDTO.class);

            DailyLog dailyLog = dailyLogDAO.findById(
                    mealDTO.getDailyLogId()
            );

            if (dailyLog == null) {
                ctx.status(404).result("DailyLog not found");
                return;
            }

            existingMeal.setMealName(mealDTO.getMealName());
            existingMeal.setDailyLog(dailyLog);

            mealDAO.update(existingMeal);

            ctx.json(toDTO(existingMeal));
        });


        // DELETE Meal
        app.delete("/meals/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            Meal meal = mealDAO.findById(id);

            if (meal == null) {
                ctx.status(404).result("Meal not found");
                return;
            }

            mealDAO.delete(id);

            ctx.status(204);
        });
    }


    // Convert Meal entity to MealDTO
    private MealDTO toDTO(Meal meal) {

        return MealDTO.builder()
                .id(meal.getId())
                .mealName(meal.getMealName())
                .dailyLogId(
                        meal.getDailyLog() != null
                                ? meal.getDailyLog().getId()
                                : null
                )
                .build();
    }
}

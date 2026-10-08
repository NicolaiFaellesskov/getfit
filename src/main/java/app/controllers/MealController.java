package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DAOs.MealDAO;
import app.DTOs.MealDTO;
import app.entities.DailyLog;
import app.entities.Meal;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MealController {

    private final MealDAO mealDAO;
    private final DailyLogDAO dailyLogDAO;

    public MealController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        this.mealDAO = new MealDAO(emf);
        this.dailyLogDAO = new DailyLogDAO(emf);
    }

    public void read(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Meal meal = mealDAO.findById(id);

        ctx.status(200);
        ctx.json(toDTO(meal));
    }

    public void readAll(Context ctx) {

        List<MealDTO> meals = mealDAO.findAll()
                .stream()
                .map(this::toDTO)
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
            ctx.status(404);
            ctx.result("DailyLog not found");
            return;
        }

        Meal meal = Meal.builder()
                .mealName(request.getMealName())
                .dailyLog(dailyLog)
                .build();

        mealDAO.save(meal);

        ctx.status(201);
        ctx.json(toDTO(meal));
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
            ctx.status(404);
            ctx.result("DailyLog not found");
            return;
        }

        Meal meal = mealDAO.findById(id);

        meal.setMealName(request.getMealName());
        meal.setDailyLog(dailyLog);

        mealDAO.update(meal);

        ctx.status(200);
        ctx.json(toDTO(meal));
    }

    public void delete(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        mealDAO.delete(id);

        ctx.status(204);
    }

    private boolean validatePrimaryKey(Integer id) {
        return mealDAO.findById(id) != null;
    }

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
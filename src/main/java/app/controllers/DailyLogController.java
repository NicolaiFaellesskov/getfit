package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;
import app.DTOs.MealDTO;
import app.entities.DailyLog;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DailyLogController {

    private final DailyLogDAO dao;

    public DailyLogController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = new DailyLogDAO(emf);
    }

    public void read(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog dailyLog = dao.findById(id);

        DailyLogDTO dto = DailyLogDTO.builder()
                .id(dailyLog.getId())
                .createdAt(dailyLog.getCreatedAt())
                .meals(
                        dailyLog.getMeals()
                                .stream()
                                .map(meal -> MealDTO.builder()
                                        .id(meal.getId())
                                        .mealName(meal.getMealName())
                                        .dailyLogId(dailyLog.getId())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();

        ctx.status(200);
        ctx.json(dto);
    }

    public void readAll(Context ctx) {

        List<DailyLogDTO> dailyLogs = dao.findAll()
                .stream()
                .map(dailyLog -> DailyLogDTO.builder()
                        .id(dailyLog.getId())
                        .createdAt(dailyLog.getCreatedAt())
                        .build())
                .toList();

        ctx.status(200);
        ctx.json(dailyLogs);
    }

    public void create(Context ctx) {

        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

        dailyLog.setCreatedAt(LocalDate.now());

        dao.save(dailyLog);

        DailyLogDTO dto = DailyLogDTO.builder()
                .id(dailyLog.getId())
                .createdAt(dailyLog.getCreatedAt())
                .build();

        ctx.status(201);
        ctx.json(dto);
    }

    public void update(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

        dailyLog.setId(id);

        dao.update(dailyLog);

        DailyLogDTO dto = DailyLogDTO.builder()
                .id(dailyLog.getId())
                .createdAt(dailyLog.getCreatedAt())
                .build();

        ctx.status(200);
        ctx.json(dto);
    }

    public void delete(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        dao.delete(id);

        ctx.status(204);
    }

    private boolean validatePrimaryKey(Integer id) {
        return dao.findById(id) != null;
    }
}
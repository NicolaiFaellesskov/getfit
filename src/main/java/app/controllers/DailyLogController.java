package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;
import app.DTOs.MealDTO;
import app.entities.DailyLog;
import app.entities.User;
import app.persistence.DailyLogMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

public class DailyLogController {

    private final DailyLogDAO dao;

    public DailyLogController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = new DailyLogDAO(emf);
    }

    public void read(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog dailyLog = dao.findById(id);

        if (!belongsToUser(dailyLog, userId)) {
            ctx.status(403).json(Map.of(
                    "error", "You do not have access to this daily log"
            ));
            return;
        }
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
        ctx.json(DailyLogMapper.toDTO(dailyLog));
    }

    public void readAll(Context ctx) {
        Integer userId = ctx.attribute("userId");


        List<DailyLogDTO> dailyLogs = dao.findByUserId(userId)
                .stream()
                .map(DailyLogMapper::toDTO)
                .toList();

        ctx.status(200);
        ctx.json(dailyLogs);
    }

    public void create(Context ctx) {
        Integer userId = ctx.attribute("userId");

        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

        User user = new User();
        user.setId(userId);
        dailyLog.setUser(user);

        dao.save(dailyLog);

        DailyLogDTO dto = DailyLogDTO.builder()
                .id(dailyLog.getId())
                .createdAt(dailyLog.getCreatedAt())
                .build();

        ctx.status(201);
        ctx.json(DailyLogMapper.toDTO(dailyLog));
    }

    public void update(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog request = ctx.bodyAsClass(DailyLog.class);
        DailyLog existing = dao.findById(id);

        existing.setCreatedAt(request.getCreatedAt() != null
                ? request.getCreatedAt()
                : existing.getCreatedAt());
        DailyLog existingLog = dao.findById(id);

        if (!belongsToUser(existingLog, userId)) {
            ctx.status(403).json(Map.of(
                    "error", "You do not have access to this daily log"
            ));
            return;
        }

        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);
        dailyLog.setId(id);
        dailyLog.setUser(existingLog.getUser());

        dao.update(existing);

        ctx.status(200);
        ctx.json(DailyLogMapper.toDTO(existing));
    }

    public void delete(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog dailyLog = dao.findById(id);

        if (!belongsToUser(dailyLog, userId)) {
            ctx.status(403).json(Map.of(
                    "error", "You do not have access to this daily log"
            ));
            return;
        }

        dao.delete(id);
        ctx.status(204);
    }

    private boolean validatePrimaryKey(Integer id) {
        return dao.findById(id) != null;
    }
    private boolean belongsToUser(DailyLog dailyLog, Integer userId) {
        return dailyLog != null
                && dailyLog.getUser() != null
                && dailyLog.getUser().getId().equals(userId);
    }
}
package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;

import app.entities.DailyLog;
import app.entities.User;
import app.persistence.DailyLogMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;


import java.util.List;


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

        ctx.status(201);
        ctx.json(DailyLogMapper.toDTO(dailyLog));
    }


    public void update(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog existing = dao.findById(id);

        if (!belongsToUser(existing, userId)) {
            ctx.status(403).json(Map.of(
                    "error", "You do not have access to this daily log"
            ));
            return;
        }

        DailyLog request = ctx.bodyAsClass(DailyLog.class);

        if (request.getCreatedAt() != null) {
            existing.setCreatedAt(request.getCreatedAt());
        }

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
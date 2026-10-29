package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;

import app.entities.DailyLog;
import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.persistence.DailyLogMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


import java.util.Map;

public class DailyLogController {
    private static final Logger logger = LoggerFactory.getLogger(DailyLogController.class);

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
        try {
            DailyLog dailyLog = dao.findById(id);
            if (!belongsToUser(dailyLog, userId)) {
                ctx.status(403).json(Map.of(
                        "error", "You do not have access to this daily log"
                ));
                return;
            }
            ctx.status(200);
            ctx.json(DailyLogMapper.toDTO(dailyLog));
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }
    }

    public void readAll(Context ctx) {
        Integer userId = ctx.attribute("userId");

        try {
            List<DailyLogDTO> dailyLogs = dao.findByUserId(userId)
                    .stream()
                    .map(DailyLogMapper::toDTO)
                    .toList();

            ctx.status(200);
            ctx.json(dailyLogs);
        }catch(DatabaseException e){
            throw new ApiException(404, e.getMessage());
        }
    }

    public void create(Context ctx) {
        Integer userId = ctx.attribute("userId");

        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

        User user = new User();
        user.setId(userId);
        dailyLog.setUser(user);
        try {
            dao.save(dailyLog);
            logger.info("Dailylog created for user {}", userId);
        } catch (DatabaseException e) {
            throw new ApiException(500, e.getMessage());
        }
        ctx.status(201);
        ctx.json(DailyLogMapper.toDTO(dailyLog));

    }


    public void update(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        try {
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
        } catch (DatabaseException e) {
            throw new ApiException(404, e.getMessage());
        }

    }

    public void delete(Context ctx) {
        Integer userId = ctx.attribute("userId");

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        try {
            DailyLog dailyLog = dao.findById(id);

            if (!belongsToUser(dailyLog, userId)) {
                ctx.status(403).json(Map.of(
                        "error", "You do not have access to this daily log"
                ));
                return;
            }

            dao.delete(id);
            logger.info("Successfully deleted dailylog from user {}", userId);
            ctx.status(204);
        }catch(DatabaseException e){
            throw new ApiException(404, e.getMessage());
        }
    }

    private boolean validatePrimaryKey(Integer id) {
        try {
            return dao.findById(id) != null;
        }catch(DatabaseException e){
            throw new ApiException(404, e.getMessage());
        }
    }

    private boolean belongsToUser(DailyLog dailyLog, Integer userId) {
        return dailyLog != null
                && dailyLog.getUser() != null
                && dailyLog.getUser().getId().equals(userId);
    }
}
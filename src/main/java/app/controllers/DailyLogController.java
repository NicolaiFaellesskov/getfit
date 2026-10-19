package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;
import app.entities.DailyLog;
import app.persistence.DailyLogMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;

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

        ctx.status(200);
        ctx.json(DailyLogMapper.toDTO(dailyLog));
    }

    public void readAll(Context ctx) {
        List<DailyLogDTO> dailyLogs = dao.findAll()
                .stream()
                .map(DailyLogMapper::toDTO)
                .toList();

        ctx.status(200);
        ctx.json(dailyLogs);
    }

    public void create(Context ctx) {
        DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

        dailyLog.setCreatedAt(LocalDate.now());

        dao.save(dailyLog);

        ctx.status(201);
        ctx.json(DailyLogMapper.toDTO(dailyLog));
    }

    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        DailyLog request = ctx.bodyAsClass(DailyLog.class);
        DailyLog existing = dao.findById(id);

        existing.setCreatedAt(request.getCreatedAt() != null
                ? request.getCreatedAt()
                : existing.getCreatedAt());

        dao.update(existing);

        ctx.status(200);
        ctx.json(DailyLogMapper.toDTO(existing));
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
package app.controllers;

import app.DAOs.DailyLogDAO;
import app.DTOs.DailyLogDTO;
import app.entities.DailyLog;
import io.javalin.Javalin;

import java.time.LocalDate;
import java.util.List;

public class DailyLogController {

    private final DailyLogDAO dailyLogDAO;

    public DailyLogController(DailyLogDAO dailyLogDAO) {
        this.dailyLogDAO = dailyLogDAO;
    }

    public void addRoutes(Javalin app) {

        // GET all DailyLogs
        app.get("/dailylogs", ctx -> {

            try {
                List<DailyLogDTO> dailyLogs = dailyLogDAO.findAll()
                        .stream()
                        .map(dailyLog -> DailyLogDTO.builder()
                                .id(dailyLog.getId())
                                .createdAt(dailyLog.getCreatedAt())
                                .build())
                        .toList();

                ctx.json(dailyLogs);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result(e.getMessage());
            }
        });


        // GET DailyLog by ID
        app.get("/dailylogs/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            DailyLog dailyLog = dailyLogDAO.findById(id);

            if (dailyLog == null) {
                ctx.status(404).result("DailyLog not found");
                return;
            }

            DailyLogDTO dto = DailyLogDTO.builder()
                    .id(dailyLog.getId())
                    .createdAt(dailyLog.getCreatedAt())
                    .build();

            ctx.json(dto);
        });


        // POST new DailyLog
        app.post("/dailylogs", ctx -> {

            DailyLog dailyLog = ctx.bodyAsClass(DailyLog.class);

            dailyLog.setCreatedAt(LocalDate.now());

            dailyLogDAO.save(dailyLog);

            DailyLogDTO dto = DailyLogDTO.builder()
                    .id(dailyLog.getId())
                    .createdAt(dailyLog.getCreatedAt())
                    .build();

            ctx.status(201).json(dto);
        });


        // PUT existing DailyLog
        app.put("/dailylogs/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            DailyLog existingDailyLog = dailyLogDAO.findById(id);

            if (existingDailyLog == null) {
                ctx.status(404).result("DailyLog not found");
                return;
            }

            DailyLog updatedDailyLog = ctx.bodyAsClass(DailyLog.class);

            updatedDailyLog.setId(id);

            dailyLogDAO.update(updatedDailyLog);

            DailyLogDTO dto = DailyLogDTO.builder()
                    .id(updatedDailyLog.getId())
                    .createdAt(updatedDailyLog.getCreatedAt())
                    .build();

            ctx.json(dto);
        });


        // DELETE DailyLog
        app.delete("/dailylogs/{id}", ctx -> {

            int id = Integer.parseInt(ctx.pathParam("id"));

            DailyLog dailyLog = dailyLogDAO.findById(id);

            if (dailyLog == null) {
                ctx.status(404).result("DailyLog not found");
                return;
            }

            dailyLogDAO.delete(id);

            ctx.status(204);
        });
    }
}

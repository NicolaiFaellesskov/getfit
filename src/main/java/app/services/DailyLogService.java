package app.services;

import app.DAOs.DailyLogDAO;
import app.entities.DailyLog;

import java.util.List;

public class DailyLogService {

    private final DailyLogDAO dailyLogDAO;

    public DailyLogService(DailyLogDAO dailyLogDAO) {
        this.dailyLogDAO = dailyLogDAO;
    }

    public void save(DailyLog dailyLog) {
        dailyLogDAO.save(dailyLog);
    }

    public DailyLog findById(int id) {
        return dailyLogDAO.findById(id);
    }

    public List<DailyLog> findAll() {
        return dailyLogDAO.findAll();
    }

    public void update(DailyLog dailyLog) {
        dailyLogDAO.update(dailyLog);
    }

    public void delete(int id) {
        dailyLogDAO.delete(id);
    }
}

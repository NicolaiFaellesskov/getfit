package app.services;

import app.DAOs.MealDAO;
import app.entities.Meal;

import java.util.List;

public class MealService {

    private final MealDAO mealDAO;

    public MealService(MealDAO mealDAO) {
        this.mealDAO = mealDAO;
    }

    public void save(Meal meal) {
        mealDAO.save(meal);
    }

    public Meal findById(int id) {
        return mealDAO.findById(id);
    }

    public List<Meal> findAll() {
        return mealDAO.findAll();
    }

    public void update(Meal meal) {
        mealDAO.update(meal);
    }

    public void delete(int id) {
        mealDAO.delete(id);
    }
}

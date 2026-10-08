package app.controllers;

import app.config.HibernateConfig;
import app.DAOs.UserDAO;
import app.entities.User;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserController {

    private final UserDAO dao;

    public UserController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = new UserDAO(emf);
    }

    public void read(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        User user = dao.findById(id);

        ctx.status(200);
        ctx.json(user);
    }

    public void readAll(Context ctx) {

        List<User> users = dao.findAll();

        ctx.status(200);
        ctx.json(users);
    }

    public void create(Context ctx) {

        User request = ctx.bodyAsClass(User.class);

        User user = dao.createUser(
                request.getUsername(),
                request.getPassword()
        );

        ctx.status(201);
        ctx.json(user);
    }

    public void update(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        User user = ctx.bodyAsClass(User.class);
        user.setId(id);

        dao.update(user);

        ctx.status(200);
        ctx.json(user);
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

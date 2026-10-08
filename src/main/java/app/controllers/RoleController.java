package app.controllers;

import app.DAOs.RoleDAO;
import app.config.HibernateConfig;
import app.entities.Role;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RoleController {

    private final RoleDAO dao;

    public RoleController() {

        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        this.dao = new RoleDAO(emf);
    }

    public void read(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Role role = dao.findById(id);

        ctx.status(200);
        ctx.json(role);
    }

    public void readAll(Context ctx) {

        List<Role> roles = dao.findAll();

        ctx.status(200);
        ctx.json(roles);
    }

    public void readByName(Context ctx) {

        String name = ctx.pathParam("name");

        Role role = dao.findByName(name);

        if (role == null) {
            ctx.status(404);
            ctx.result("Role not found");
            return;
        }

        ctx.status(200);
        ctx.json(role);
    }

    public void create(Context ctx) {

        Role request = ctx.bodyAsClass(Role.class);

        Role role = dao.save(request);

        ctx.status(201);
        ctx.json(role);
    }

    public void update(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Role role = ctx.bodyAsClass(Role.class);

        role.setId(id);

        Role updatedRole = dao.update(role);

        ctx.status(200);
        ctx.json(updatedRole);
    }

    public void delete(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        boolean deleted = dao.delete(id);

        if (!deleted) {
            ctx.status(404);
            ctx.result("Role not found");
            return;
        }

        ctx.status(204);
    }

    private boolean validatePrimaryKey(Integer id) {
        return dao.findById(id) != null;
    }
}
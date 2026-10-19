package app.controllers;

import app.DTOs.UserDTO;
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

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRolesAsStrings())
                .build();

        ctx.status(200);
        ctx.json(dto);
    }

    public void readAll(Context ctx) {

        List<UserDTO> users = dao.findAll()
                .stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .roles(user.getRolesAsStrings())
                        .build())
                .toList();

        ctx.status(200);
        ctx.json(users);
    }

    public void create(Context ctx) {

        User request = ctx.bodyAsClass(User.class);

        User user = dao.createUser(
                request.getUsername(),
                request.getPassword()
        );

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();

        ctx.status(201);
        ctx.json(dto);
    }

    public void update(Context ctx) {

        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        User request = ctx.bodyAsClass(User.class);
        User existingUser = dao.findById(id);

        existingUser.setUsername(request.getUsername());

        // Opdater kun password, hvis et nyt password er sendt.
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            existingUser.setPassword(
                    org.mindrot.jbcrypt.BCrypt.hashpw(
                            request.getPassword(),
                            org.mindrot.jbcrypt.BCrypt.gensalt()
                    )
            );
        }

        dao.update(existingUser);

        UserDTO dto = UserDTO.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .roles(existingUser.getRolesAsStrings())
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

    public void addUserRole(Context ctx) {

        String username = ctx.pathParam("username");

        RoleRequest request = ctx.bodyAsClass(RoleRequest.class);

        User user = dao.addUserRole(
                username,
                request.role()
        );

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRolesAsStrings())
                .build();

        ctx.status(200);
        ctx.json(dto);
    }

    private boolean validatePrimaryKey(Integer id) {
        return dao.findById(id) != null;
    }


    public record RoleRequest(String role) {}
}
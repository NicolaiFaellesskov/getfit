
package app.controllers;

import app.DTOs.LoginRequest;
import app.DAOs.UserDAO;
import app.entities.User;
import app.config.HibernateConfig;
import app.security.TokenService;
import app.exceptions.ValidationException;
import io.javalin.http.Context;

import java.util.Map;

public class LoginController {

    private final UserDAO userDAO;
    private final TokenService tokenService;

    public LoginController() {
        this.userDAO = new UserDAO(
                HibernateConfig.getEntityManagerFactory()
        );
        this.tokenService = new TokenService();
    }

    public void login(Context ctx) {
        try {
            LoginRequest request =
                    ctx.bodyAsClass(LoginRequest.class);

            User user = userDAO.getVerifiedUser(
                    request.username(),
                    request.password()
            );

            String token =
                    tokenService.generateToken(user.getId());

            ctx.status(200).json(Map.of(
                    "token", token,
                    "tokenType", "Bearer"
            ));

        } catch (ValidationException e) {
            ctx.status(401).json(Map.of(
                    "error", "Invalid username or password"
            ));
        }
    }
}
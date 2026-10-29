package app.security;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.Map;

public class SecurityController implements ISecurityController {

    private final TokenService tokenService = new TokenService();

    @Override
    public void authenticate(Context ctx) {
        // CORS preflight
        if (ctx.method() == HandlerType.OPTIONS) {
            return;
        }

        String path = ctx.path();
        HandlerType method = ctx.method();

        // Offentlige endpoints
        if (path.endsWith("/auth/login")) {
            return;
        }
        if (path.endsWith("/users") && method == HandlerType.POST) {
            return;
        }

        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(Map.of(
                    "error", "Missing or invalid Authorization header"
            ));
            ctx.skipRemainingHandlers();
            return;
        }

        try {
            String token = authHeader.substring(7);
            Integer userId = tokenService.validateToken(token);
            ctx.attribute("userId", userId);
        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of(
                    "error", "Invalid or expired token"
            ));
            ctx.skipRemainingHandlers();
        }
    }

    @Override
    public void authorize(Context ctx) {
        // Ingen roller endnu – intet at tjekke
    }
}
package app.config;

import app.exceptions.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import app.security.TokenService;
import io.javalin.http.HandlerType;

import java.util.Map;

public class ApplicationConfig {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private final List<EndpointGroup> routes = new ArrayList<>();
    private final List<Consumer<JavalinConfig>> configSteps = new ArrayList<>();

    private Javalin app;

    public ApplicationConfig() {
        configSteps.add(this::applyBaseConfig);
    }

    public ApplicationConfig route(EndpointGroup route) {
        routes.add(route);
        return this;
    }

    public ApplicationConfig cors() {
        configSteps.add(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> rule.anyHost());
            });
        });

        return this;
    }


    public ApplicationConfig security() {
        configSteps.add(config -> {

            TokenService tokenService = new TokenService();

            config.routes.before(ctx -> {

                String path = ctx.path();
                HandlerType method = ctx.method();

                // Allow CORS preflight requests
                if (method == HandlerType.OPTIONS) {
                    return;
                }

                // Login must be public
                if (path.endsWith("/auth/login")) {
                    return;
                }

                // Allow new users to register
                if (path.endsWith("/users")
                        && method == HandlerType.POST) {
                    return;
                }

                // Read the Authorization header
                String authHeader = ctx.header("Authorization");

                if (authHeader == null
                        || !authHeader.startsWith("Bearer ")) {

                    ctx.status(401).json(Map.of(
                            "error", "Missing or invalid Authorization header"
                    ));

                    ctx.skipRemainingHandlers();
                    return;
                }

                // Validate the JWT
                try {
                    String token = authHeader.substring(7);

                    Integer userId =
                            tokenService.validateToken(token);

                    ctx.attribute("userId", userId);

                } catch (IllegalArgumentException e) {

                    ctx.status(401).json(Map.of(
                            "error", "Invalid or expired token"
                    ));

                    ctx.skipRemainingHandlers();
                }
            });
        });

        return this;
    }

    public ApplicationConfig apiExceptions() {
        configSteps.add(config ->
                config.routes.exception(ApiException.class, (e, ctx) -> {

                    int statusCode = e.getCode();

                    ObjectNode body = jsonMapper.createObjectNode()
                            .put("status", statusCode)
                            .put("msg", e.getMessage());

                    ctx.status(statusCode).json(body);
                })
        );

        return this;
    }

    public ApplicationConfig exceptions() {
        configSteps.add(config ->
                config.routes.exception(Exception.class, (e, ctx) -> {

                    ObjectNode body = jsonMapper.createObjectNode()
                            .put("status", 500)
                            .put("msg", "An unexpected error occurred");

                    logger.error("Unhandled exception", e);

                    ctx.status(500).json(body);
                })
        );

        return this;
    }

    public ApplicationConfig notFound() {
        configSteps.add(config ->
                config.routes.error(404, ctx -> {

                    String message = ctx.attribute("msg");

                    ObjectNode body = jsonMapper.createObjectNode()
                            .put("msg", message == null ? "Not found" : message);

                    ctx.json(body);
                })
        );

        return this;
    }

    public ApplicationConfig requestLogger() {
        configSteps.add(config ->
                config.routes.before(ctx -> {

                    ctx.req()
                            .getHeaderNames()
                            .asIterator()
                            .forEachRemaining(System.out::println);
                })
        );

        return this;
    }

    public Javalin start(int port) {

        app = Javalin.create(config -> {

            for (Consumer<JavalinConfig> step : configSteps) {
                step.accept(config);
            }

            for (EndpointGroup route : routes) {
                config.routes.apiBuilder(route);
            }
        });

        app.start(port);

        return app;
    }

    public void stopServer() {
        if (app != null) {
            app.stop();
            app = null;
        }
    }

    private void applyBaseConfig(JavalinConfig config) {

        config.http.defaultContentType = "application/json";

        config.router.contextPath = "/api";

        config.bundledPlugins.enableDevLogging();

        config.bundledPlugins.enableRouteOverview("/routes");

        config.events.serverStarted(() ->
                System.out.println(
                        "Javalin started on http://localhost:" + 7070 + "/api"
                )
        );

        config.events.serverStopped(() ->
                System.out.println("Javalin stopped")
        );
    }
}
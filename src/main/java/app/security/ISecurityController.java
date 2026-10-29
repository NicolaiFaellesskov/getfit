package app.security;

import io.javalin.http.Context;

public interface ISecurityController {
    void authenticate(Context ctx);
    void authorize(Context ctx);
}
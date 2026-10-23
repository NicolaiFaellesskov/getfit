package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthApiTest {

    private static final String BASE_URI =
            "http://localhost:7070/api";

    @Test
    void loginWithValidCredentialsReturnsToken() {
        given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body("""
                {
                    "username": "jwtTestA",
                    "password": "Test1234!"
                }
                """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", not(emptyOrNullString()))
                .body("tokenType", equalTo("Bearer"));
    }

    @Test
    void loginWithInvalidCredentialsReturns401() {
        given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body("""
                {
                    "username": "jwtTestA",
                    "password": "Wrongpassword!"
                }
                """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("error",
                        equalTo("Invalid username or password"));
    }
}
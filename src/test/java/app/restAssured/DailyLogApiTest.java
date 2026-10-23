package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class DailyLogApiTest {

    private static final String BASE_URI =
            "http://localhost:7070/api";

    private String getToken() {
        return given()
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
                .extract()
                .path("token");
    }

    @Test
    void getAllDailyLogsReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/dailylogs")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", notNullValue());
    }

    @Test
    void createDailyLogReturns201() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "createdAt": "2026-10-23"
                    }
                    """)
                .when()
                .post("/dailylogs")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue());
    }

    @Test
    void getDailyLogWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/dailylogs/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }

    @Test
    void updateDailyLogWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                {
                    "createdAt": "2026-10-22"
                }
                """)
                .when()
                .put("/dailylogs/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }

    @Test
    void deleteDailyLogWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .delete("/dailylogs/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));

    }

    @Test
    void getDailyLogsWithoutTokenReturns401() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/dailylogs")
                .then()
                .statusCode(401);
    }

    @Test
    void createDailyLogWithoutTokenReturns401() {
        given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body("""
                    {
                        "createdAt": "2026-10-23"
                    }
                    """)
                .when()
                .post("/dailylogs")
                .then()
                .statusCode(401);
    }
}
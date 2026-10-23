package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class MealApiTest {

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
    void getAllMealsReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/meals")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", notNullValue());
    }

    @Test
    void createMealWithNonExistingDailyLogReturns404() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                {
                    "mealName": "Test Breakfast",
                    "dailyLogId": 999999999
                }
                """)
                .when()
                .post("/meals")
                .then()
                .statusCode(404)
                .body(equalTo("DailyLog not found"));
    }

    @Test
    void addFoodToNonExistingMealReturns404() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/meals/999999999/food")
                .then()
                .statusCode(404)
                .body(equalTo("Meal not found"));
    }

    @Test
    void getMealWithoutTokenReturns401() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/meals")
                .then()
                .statusCode(401);
    }
}
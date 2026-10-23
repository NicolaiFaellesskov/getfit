package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class FoodApiTest {

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
                .body("tokenType", equalTo("Bearer"))
                .extract()
                .path("token");
    }

    @Test
    void searchFoodWithValidTokenReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .queryParam("search", "apple")
                .when()
                .get("/food/food")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", notNullValue());
    }

    @Test
    void searchFoodWithoutSearchParameterReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/food/food")
                .then()
                .statusCode(400)
                .body(equalTo("Missing search parameter"));
    }

    @Test
    void searchFoodWithEmptySearchParameterReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .queryParam("search", "")
                .when()
                .get("/food/food")
                .then()
                .statusCode(400)
                .body(equalTo("Missing search parameter"));
    }

    @Test
    void searchFoodWithoutTokenReturns401() {
        given()
                .baseUri(BASE_URI)
                .queryParam("search", "apple")
                .when()
                .get("/food/food")
                .then()
                .statusCode(401);
    }
}
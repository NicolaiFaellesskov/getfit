package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class UserApiTest {

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
    void getAllUsersReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", notNullValue());
    }

    @Test
    void getUserWithExistingIdReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(1))
                .body("username", notNullValue());
    }

    @Test
    void getUserWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/users/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }

    @Test
    void createUserReturns201() {
        String token = getToken();

        String username = "restAssuredUser_" + System.nanoTime();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "username": "%s",
                        "password": "Test1234!"
                    }
                    """.formatted(username))
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("username", equalTo(username));
    }

    @Test
    void updateUserReturns200() {
        String token = getToken();

        String username = "updatedUser_" + System.nanoTime();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "username": "%s"
                    }
                    """.formatted(username))
                .when()
                .put("/users/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(1))
                .body("username", equalTo(username));
    }

    @Test
    void updateUserWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "username": "updatedUser"
                    }
                    """)
                .when()
                .put("/users/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }

    @Test
    void deleteUserWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .delete("/users/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }

    @Test
    void getUsersWithoutTokenReturns401() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/users")
                .then()
                .statusCode(401);
    }

    @Test
    void createUserWithoutTokenReturns201() {
        String username = "unauthorizedUser_" + System.nanoTime();

        given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body("""
                {
                    "username": "%ss",
                    "password": "Test1234!"
                }
                """.formatted(username))
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("username", equalTo(username));
    }
}
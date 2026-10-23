package app.restAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class RoleApiTest {

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
    void getAllRolesReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/roles")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", notNullValue());
    }

    @Test
    void getRoleWithExistingIdReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/roles/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(1))
                .body("name", notNullValue());
    }

    @Test
    void getRoleByNameReturns200() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/roles/name/USER")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("name", equalTo("USER"));
    }

    @Test
    void getRoleByNonExistingNameReturns404() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .get("/roles/name/NON_EXISTING_ROLE")
                .then()
                .statusCode(404)
                .body(equalTo("Role not found"));
    }

    @Test
    void createRoleReturns201() {
        String token = getToken();

        String roleName = "TEST_ROLE_" + System.nanoTime();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "name": "%s"
                    }
                    """.formatted(roleName))
                .when()
                .post("/roles")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("name", equalTo(roleName));
    }

    @Test
    void updateRoleReturns200() {
        String token = getToken();

        String roleName = "UPDATED_ROLE_" + System.nanoTime();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                    {
                        "name": "%s"
                    }
                    """.formatted(roleName))
                .when()
                .put("/roles/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(1))
                .body("name", equalTo(roleName));
    }

    @Test
    void deleteRoleWithNonExistingIdReturns400() {
        String token = getToken();

        given()
                .baseUri(BASE_URI)
                .auth().oauth2(token)
                .when()
                .delete("/roles/999999999")
                .then()
                .statusCode(400)
                .body("id[0].message", equalTo("Not a valid id"));
    }
}
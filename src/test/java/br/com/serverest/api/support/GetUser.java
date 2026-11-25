package br.com.serverest.api.support;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

public class GetUser {

    public static void getUser(String userId, String name) {
        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("_id", is(userId)).
            body("nome", is(name));
    }

    public static void getUser(String userId) {
        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("_id", is(userId));
    }

    public static void getUserWithEmail(String userId, String email) {
        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("_id", is(userId)).
            body("email", is(email));
    }

    public static void getUserNotFound(String userId) {
        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("message", is("Usuário não encontrado"));
    }
}
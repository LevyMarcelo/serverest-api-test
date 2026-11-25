package br.com.serverest.api.support;

import io.restassured.http.ContentType;

import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.validAdminUser;
import static br.com.serverest.api.factory.UserDataFactory.validUser;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;

public final class CreateUser extends BaseAPI {

    private CreateUser() {}

    public static Map<String, Object> createUser() {
        return createUser(validUser());
    }

    public static Map<String, Object> createAdminUser() {
        return createUser(validAdminUser());
    }

    private static Map<String, Object> createUser(Map<String, Object> user) {
        String userId =
                given().
                    contentType(ContentType.JSON).
                    body(user).
                when().
                    post("/usuarios").
                then().
                    statusCode(SC_CREATED).
                    extract().
                        path("_id");

        user.put("_id", userId);

        return user;
    }
}

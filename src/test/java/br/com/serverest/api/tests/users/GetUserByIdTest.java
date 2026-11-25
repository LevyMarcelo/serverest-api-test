package br.com.serverest.api.tests.users;

import br.com.serverest.api.support.BaseAPI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.invalidUserId;
import static br.com.serverest.api.factory.UserDataFactory.missingUserId;
import static br.com.serverest.api.support.CreateUser.createUser;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

class GetUserByIdTest extends BaseAPI {

    private static Map<String, Object> user;

    @BeforeAll
    static void setUp() {
        user = createUser();
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-026 - Consultar usuário existente por ID")
    void shouldFindExistingUserById() {
        String userId = user.get("_id").toString();

        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("_id", is(userId)).
            body("nome", is(user.get("nome"))).
            body("email", is(user.get("email"))).
            body("password", is(user.get("password"))).
            body("administrador", is(user.get("administrador")));
    }

    @Test
    @Tag("contract")
    @DisplayName("CT-027 - Validar contrato da resposta ao consultar usuário por ID")
    void shouldMatchUserByIdResponseSchema() {
        String userId = user.get("_id").toString();

        given().
            pathParam("_id", userId).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body(matchesJsonSchemaInClasspath("schemas/users/get-user-by-id-success.json"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-028 - Falha ao consultar usuário com ID inválido")
    void shouldRejectInvalidUserId() {
        given().
            pathParam("_id", invalidUserId()).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("id", is("id deve ter exatamente 16 caracteres alfanuméricos"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-029 - Falha ao consultar usuário inexistente")
    void shouldRejectMissingUser() {
        given().
            pathParam("_id", missingUserId()).
        when().
            get("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("message", is("Usuário não encontrado"));
    }
}

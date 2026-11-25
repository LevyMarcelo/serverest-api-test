package br.com.serverest.api.tests.users;

import br.com.serverest.api.support.BaseAPI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.uniqueEmail;
import static br.com.serverest.api.support.CreateUser.createAdminUser;
import static br.com.serverest.api.support.CreateUser.createUser;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

class GetUsersTest extends BaseAPI {

    private static Map<String, Object> user;
    private static Map<String, Object> adminUser;

    @BeforeAll
    static void setUp() {
        user = createUser();
        adminUser = createAdminUser();
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-015 - Consultar todos os usuários cadastrados sem aplicar filtros")
    void shouldListUsersWithoutFilters() {
        String userId = user.get("_id").toString();
        String adminUserId = adminUser.get("_id").toString();

        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("quantidade", greaterThanOrEqualTo(0)).
            body("usuarios", instanceOf(List.class)).
            body("usuarios._id", hasItems(userId, adminUserId));
    }

    @Test
    @Tag("contract")
    @DisplayName("CT-016 - Validar contrato da resposta ao consultar usuários")
    void shouldMatchUsersResponseSchema() {
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body(matchesJsonSchemaInClasspath("schemas/users/get-users-success.json"));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-017 - Consultar usuários filtrando por ID")
    void shouldFindUserById() {
        String userId = user.get("_id").toString();

        given().
            queryParam("_id", userId).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios._id", is(List.of(userId)));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-018 - Consultar usuários filtrando por nome")
    void shouldFindUsersByName() {
        String name = user.get("nome").toString();

        given().
            queryParam("nome", name).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.nome", not(empty())).
            body("usuarios.nome", everyItem(containsString(name)));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-019 - Consultar usuários filtrando por e-mail")
    void shouldFindUsersByEmail() {
        String email = user.get("email").toString();

        given().
            queryParam("email", email).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.email", not(empty())).
            body("usuarios.email", everyItem(is(email)));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-020 - Consultar usuários filtrando por senha")
    void shouldFindUsersByPassword() {
        String password = user.get("password").toString();

        given().
            queryParam("password", password).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.password", not(empty())).
            body("usuarios.password", everyItem(is(password)));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-021 - Consultar usuários administradores")
    void shouldFindAdministratorUsers() {
        String adminUserId = adminUser.get("_id").toString();

        given().
            queryParam("administrador", "true").
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.administrador", not(empty())).
            body("usuarios.administrador", everyItem(is("true"))).
            body("usuarios._id", hasItem(adminUserId));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-022 - Consultar usuários não administradores")
    void shouldFindRegularUsers() {
        String userId = user.get("_id").toString();

        given().
            queryParam("administrador", "false").
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.administrador", not(empty())).
            body("usuarios.administrador", everyItem(is("false"))).
            body("usuarios._id", hasItem(userId));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-023 - Consultar usuários combinando filtros de nome e e-mail")
    void shouldFindUsersByNameAndEmail() {
        String name = user.get("nome").toString();
        String email = user.get("email").toString();

        given().
            queryParam("nome", name).
            queryParam("email", email).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("usuarios.nome", not(empty())).
            body("usuarios.email", not(empty())).
            body("usuarios.nome", everyItem(is(name))).
            body("usuarios.email", everyItem(is(email)));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-024 - Consultar usuários sem registros correspondentes aos filtros informados")
    void shouldReturnEmptyListWhenNoUsersMatch() {
        given().
            queryParam("email", uniqueEmail()).
        when().
            get("/usuarios").
        then().
            statusCode(SC_OK).
            body("quantidade", is(0)).
            body("usuarios", is(empty()));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-025 - Falha ao consultar usuários com e-mail em formato inválido no filtro")
    void shouldRejectInvalidEmailFilter() {
        given().
            queryParam("email", "email-invalido").
        when().
            get("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("email", is("email deve ser um email válido"));
    }
}

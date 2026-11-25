package br.com.serverest.api.tests.users;

import br.com.serverest.api.support.BaseAPI;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.*;
import static br.com.serverest.api.support.CreateUser.createUser;
import static br.com.serverest.api.support.GetUser.getUser;
import static br.com.serverest.api.support.GetUser.getUserWithEmail;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

class PutUserTest extends BaseAPI {

    private Map<String, Object> user;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-030 - Atualizar usuário existente")
    void shouldUpdateExistingUser() {
        String userId = user.get("_id").toString();
        String updatedName = "Usuário de teste atualizado";
        user.remove("_id"); // Remove o ID retornado pelo método de criação de usuário
        user.put("nome", updatedName);

        given().
            contentType(ContentType.JSON).
            pathParam("_id", userId).
            body(user).
        when().
            put("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("message", is("Registro alterado com sucesso"));

        getUser(userId, updatedName);
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-031 - Cadastrar usuário quando o ID informado não existe")
    void shouldCreateUserWhenIdDoesNotExist() {
        String createdUserId =
            given().
                contentType(ContentType.JSON).
                pathParam("_id", missingUserId()).
                body(validUser()).
            when().
                put("/usuarios/{_id}").
            then().
                statusCode(SC_CREATED).
                body("message", is("Cadastro realizado com sucesso")).
                body("_id", not(isEmptyOrNullString())).
                extract().
                    path("_id");

        getUser(createdUserId);
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-032 - Rejeitar atualização com campos obrigatórios vazios")
    void shouldRejectUpdateWithEmptyRequiredFields() {
        String userId = user.get("_id").toString();

        given().
            contentType(ContentType.JSON).
            pathParam("_id", userId).
            body(userWithEmptyFields()).
        when().
            put("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome não pode ficar em branco")).
            body("email", is("email não pode ficar em branco")).
            body("password", is("password não pode ficar em branco")).
            body("administrador", is("administrador deve ser 'true' ou 'false'"));

        getUser(userId, user.get("nome").toString());
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-033 - Rejeitar atualização com e-mail já utilizado")
    void shouldRejectUpdateWithDuplicateEmail() {
        String userId = user.get("_id").toString();
        String originalEmail = user.get("email").toString();
        Map<String, Object> otherUser = createUser();
        user.remove("_id"); // Remove o ID retornado pelo método de criação de usuário
        user.put("email", otherUser.get("email"));

        given().
            contentType(ContentType.JSON).
            pathParam("_id", userId).
            body(user).
        when().
            put("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("message", is("Este email já está sendo usado"));

        getUserWithEmail(userId, originalEmail);
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-034 - Rejeitar atualização com tipos de dados incorretos")
    void shouldRejectUpdateWithInvalidFieldTypes() {
        String userId = user.get("_id").toString();

        given().
            contentType(ContentType.JSON).
            pathParam("_id", userId).
            body(userWithInvalidTypes()).
        when().
            put("/usuarios/{_id}").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome deve ser uma string")).
            body("email", is("email deve ser uma string")).
            body("password", is("password deve ser uma string")).
            body("administrador", is("administrador deve ser 'true' ou 'false'"));

        getUser(userId, user.get("nome").toString());
    }
}

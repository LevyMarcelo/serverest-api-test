package br.com.serverest.api.tests.users;

import br.com.serverest.api.support.BaseAPI;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.userWithAdditionalProperty;
import static br.com.serverest.api.factory.UserDataFactory.userWithEmptyFields;
import static br.com.serverest.api.factory.UserDataFactory.userWithInvalidAdministrator;
import static br.com.serverest.api.factory.UserDataFactory.userWithInvalidEmail;
import static br.com.serverest.api.factory.UserDataFactory.userWithInvalidTypes;
import static br.com.serverest.api.factory.UserDataFactory.userWithoutAdministrator;
import static br.com.serverest.api.factory.UserDataFactory.userWithoutEmail;
import static br.com.serverest.api.factory.UserDataFactory.userWithoutName;
import static br.com.serverest.api.factory.UserDataFactory.userWithoutPassword;
import static br.com.serverest.api.factory.UserDataFactory.validAdminUser;
import static br.com.serverest.api.factory.UserDataFactory.validUser;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class PostUsersTest extends BaseAPI {

    @Test
    @Tag("positive")
    @DisplayName("CT-001 - Cadastrar usuário comum com sucesso")
    void shouldCreateRegularUserSuccessfully() {
        given().
            contentType(ContentType.JSON).
            body(validUser()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_CREATED).
            body("message", is("Cadastro realizado com sucesso")).
            body("_id", not(isEmptyOrNullString()));
    }

    @Test
    @Tag("positive")
    @DisplayName("CT-002 - Cadastrar usuário administrador com sucesso")
    void shouldCreateAdministratorUserSuccessfully() {
        given().
            contentType(ContentType.JSON).
            body(validAdminUser()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_CREATED).
            body("message", is("Cadastro realizado com sucesso")).
            body("_id", not(isEmptyOrNullString()));
    }

    @Test
    @Tag("contract")
    @DisplayName("CT-003 - Validar contrato da resposta de cadastro de usuário")
    void shouldMatchUserCreationResponseSchema() {
        given().
            contentType(ContentType.JSON).
            body(validUser()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_CREATED).
            body(matchesJsonSchemaInClasspath("schemas/users/create-user-success.json"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-004 - Falha ao cadastrar usuário com e-mail já cadastrado")
    void shouldRejectDuplicateEmail() {
        Map<String, Object> user = validUser();

        given().
            contentType(ContentType.JSON).
            body(user).
        when().
            post("/usuarios").
        then().
            statusCode(SC_CREATED).
            body("message", is("Cadastro realizado com sucesso")).
            body("_id", not(isEmptyOrNullString()));

        given().
            contentType(ContentType.JSON).
            body(user).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("message", is("Este email já está sendo usado"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-005 - Falha ao cadastrar usuário sem informar o nome")
    void shouldRejectUserWithoutName() {
        given().
            contentType(ContentType.JSON).
            body(userWithoutName()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome é obrigatório"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-006 - Falha ao cadastrar usuário sem informar o e-mail")
    void shouldRejectUserWithoutEmail() {
        given().
            contentType(ContentType.JSON).
            body(userWithoutEmail()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("email", is("email é obrigatório"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-007 - Falha ao cadastrar usuário sem informar a senha")
    void shouldRejectUserWithoutPassword() {
        given().
            contentType(ContentType.JSON).
            body(userWithoutPassword()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("password", is("password é obrigatório"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-008 - Falha ao cadastrar usuário sem informar o campo administrador")
    void shouldRejectUserWithoutAdministrator() {
        given().
            contentType(ContentType.JSON).
            body(userWithoutAdministrator()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("administrador", is("administrador é obrigatório"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-009 - Falha ao cadastrar usuário com corpo vazio")
    void shouldRejectEmptyRequestBody() {
        given().
            contentType(ContentType.JSON).
            body(Map.of()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome é obrigatório")).
            body("email", is("email é obrigatório")).
            body("password", is("password é obrigatório")).
            body("administrador", is("administrador é obrigatório"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-010 - Falha ao cadastrar usuário com e-mail em formato inválido")
    void shouldRejectUserWithInvalidEmail() {
        given().
            contentType(ContentType.JSON).
            body(userWithInvalidEmail()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("email", is("email deve ser um email válido"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-011 - Falha ao cadastrar usuário com valor inválido no campo administrador")
    void shouldRejectUserWithInvalidAdministrator() {
        given().
            contentType(ContentType.JSON).
            body(userWithInvalidAdministrator()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("administrador", is("administrador deve ser 'true' ou 'false'"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-012 - Falha ao cadastrar usuário com campos obrigatórios vazios")
    void shouldRejectUserWithEmptyRequiredFields() {
        given().
            contentType(ContentType.JSON).
            body(userWithEmptyFields()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome não pode ficar em branco")).
            body("email", is("email não pode ficar em branco")).
            body("password", is("password não pode ficar em branco")).
            body("administrador", is("administrador deve ser 'true' ou 'false'"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-013 - Falha ao cadastrar usuário com tipos de dados incorretos")
    void shouldRejectUserWithInvalidFieldTypes() {
        given().
            contentType(ContentType.JSON).
            body(userWithInvalidTypes()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("nome", is("nome deve ser uma string")).
            body("email", is("email deve ser uma string")).
            body("password", is("password deve ser uma string")).
            body("administrador", is("administrador deve ser 'true' ou 'false'"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-014 - Falha ao cadastrar usuário com propriedades adicionais no payload")
    void shouldRejectUserWithAdditionalProperty() {
        given().
            contentType(ContentType.JSON).
            body(userWithAdditionalProperty()).
        when().
            post("/usuarios").
        then().
            statusCode(SC_BAD_REQUEST).
            body("campoExtra", is("campoExtra não é permitido"));
    }
}

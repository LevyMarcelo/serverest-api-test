package br.com.serverest.api.tests.users;

import br.com.serverest.api.support.BaseAPI;
import br.com.serverest.api.support.CreateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static br.com.serverest.api.factory.UserDataFactory.invalidUserId;
import static br.com.serverest.api.factory.UserDataFactory.missingUserId;
import static br.com.serverest.api.support.GetUser.getUserNotFound;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

class DeleteUserTest extends BaseAPI {

    @Test
    @Tag("positive")
    @DisplayName("CT-035 - Excluir usuário existente")
    void shouldDeleteExistingUser() {
        Map<String, Object> user = CreateUser.createUser();
        String userId = user.get("_id").toString();

        given().
            pathParam("_id", userId).
        when().
            delete("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("message", not(isEmptyOrNullString()));

        getUserNotFound(userId);
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-036 - Rejeitar exclusão com ID inválido")
    void shouldReportNoDeletionForInvalidUserId() {
        given().
            pathParam("_id", invalidUserId()).
        when().
            delete("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("message", is("Nenhum registro excluído"));
    }

    @Test
    @Tag("negative")
    @DisplayName("CT-037 - Tentar excluir usuário inexistente")
    void shouldNotDeleteMissingUser() {
        String missingUserId = missingUserId();

        given().
            pathParam("_id", missingUserId).
        when().
            delete("/usuarios/{_id}").
        then().
            statusCode(SC_OK).
            body("message", not(isEmptyOrNullString()));

        getUserNotFound(missingUserId);
    }
}

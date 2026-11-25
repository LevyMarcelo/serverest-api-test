package br.com.serverest.api.factory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class UserDataFactory {

    private UserDataFactory() {
    }

    public static Map<String, Object> validUser() {
        return validUser("false");
    }

    public static Map<String, Object> validAdminUser() {
        return validUser("true");
    }

    public static Map<String, Object> validUser(String administrator) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("nome", "Usuário de teste");
        user.put("email", uniqueEmail());
        user.put("password", "senha-teste-123");
        user.put("administrador", administrator);

        return user;
    }

    public static Map<String, Object> userWithoutName() {
        Map<String, Object> user = validUser();
        user.remove("nome");
        return user;
    }

    public static Map<String, Object> userWithoutEmail() {
        Map<String, Object> user = validUser();
        user.remove("email");
        return user;
    }

    public static Map<String, Object> userWithoutPassword() {
        Map<String, Object> user = validUser();
        user.remove("password");
        return user;
    }

    public static Map<String, Object> userWithoutAdministrator() {
        Map<String, Object> user = validUser();
        user.remove("administrador");
        return user;
    }

    public static Map<String, Object> userWithInvalidEmail() {
        Map<String, Object> user = validUser();
        user.put("email", "email-invalido");
        return user;
    }

    public static Map<String, Object> userWithInvalidAdministrator() {
        Map<String, Object> user = validUser();
        user.put("administrador", "yes");
        return user;
    }

    public static Map<String, Object> userWithEmptyFields() {
        Map<String, Object> user = validUser();
        user.put("nome", "");
        user.put("email", "");
        user.put("password", "");
        user.put("administrador", "");
        return user;
    }

    public static Map<String, Object> userWithInvalidTypes() {
        Map<String, Object> user = validUser();
        user.put("nome", 123);
        user.put("email", true);
        user.put("password", 123);
        user.put("administrador", false);
        return user;
    }

    public static Map<String, Object> userWithAdditionalProperty() {
        Map<String, Object> user = validUser();
        user.put("campoExtra", "valor");
        return user;
    }

    /**
     * Gera um endereço de e-mail único para utilização nos dados de teste.
     *
     * Utiliza um UUID aleatório para evitar duplicidade de e-mails
     * entre diferentes execuções dos testes.
     * O prefixo "qa." identifica o endereço como dado de teste,
     * enquanto "@example.com" utiliza um domínio reservado para exemplos.
     *
     * Exemplo: qa.550e8400-e29b-41d4-a716-446655440000@example.com
     *
     * @return endereço de e-mail único em formato válido
     */
    public static String uniqueEmail() {
        return "qa." + UUID.randomUUID() + "@example.com";
    }

    /**
     * Gera um identificador de usuário com formato inválido para cenários negativos.
     *
     * Utiliza a representação padrão de um UUID, que contém 36 caracteres,
     * incluindo quatro hífens.
     *
     * A API ServeRest espera identificadores com exatamente 16 caracteres
     * alfanuméricos. Portanto, o valor gerado possui intencionalmente
     * um formato inválido para esse requisito.
     *
     * @return identificador UUID com formato inválido para o usuário
     */
    public static String invalidUserId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gera um identificador com formato válido, mas sem usuário associado.
     *
     * Gera um UUID aleatório, remove os hífens e extrai
     * os primeiros 16 caracteres hexadecimais.
     *
     * O resultado possui exatamente 16 caracteres alfanuméricos,
     * atendendo ao formato esperado pela API ServeRest.
     * O identificador é utilizado para testar consultas de usuários inexistentes.
     *
     * Observação: a geração aleatória torna extremamente improvável
     * uma colisão com um usuário existente, mas não garante matematicamente
     * que o identificador não esteja cadastrado.
     *
     * @return identificador aleatório de 16 caracteres
     */
    public static String missingUserId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
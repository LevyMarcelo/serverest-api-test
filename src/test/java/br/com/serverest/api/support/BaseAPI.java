package br.com.serverest.api.support;

import org.junit.jupiter.api.BeforeAll;
import static io.restassured.RestAssured.baseURI;

public abstract class BaseAPI {

    @BeforeAll
    public static void beforeAllTests() {
        baseURI = "https://serverest.dev";
    }
}

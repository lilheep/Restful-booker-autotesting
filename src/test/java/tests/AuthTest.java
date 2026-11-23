package tests;

import clients.AuthClient;
import config.Config;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@Epic("Restful-booker")
@Feature("Auth")
public class AuthTest extends BaseTest {

    private final AuthClient auth = new AuthClient();

    @Test
    @Tag("smoke")
    @DisplayName("Valid credentials return a token matching the schema")
    void validCredentialsReturnToken() {

        auth.login(Config.USERNAME, Config.PASSWORD)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/auth-token.json"));

    }

    @ParameterizedTest(name = "invalid credentials: {0} / {1}")
    @CsvSource({
            "admin, wrongpassword",
            "wronguser, password123",
            "wronguser, wrongpassword"
    })
    @Tag("known-issue")
    @DisplayName("Invalid credentials return a reason instead of a token")
    void invalidCredentialsReturnReason(String username, String password) {

        auth.login(username, password)
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"))
                .body("token", nullValue());

    }

}

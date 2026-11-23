package tests;

import clients.Specs;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Epic("Restful-booker")
@Feature("Health")
@Tag("smoke")
public class PingTest extends BaseTest {

    @Test
    @DisplayName("Service responds to ping with 201")
    void servicePing() {

        given()
                .spec(Specs.base())
                .when()
                .get("/ping")
                .then()
                .statusCode(201);

    }


}

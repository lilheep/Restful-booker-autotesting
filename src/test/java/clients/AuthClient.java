package clients;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.AuthRequest;
import models.AuthResponse;

import static io.restassured.RestAssured.given;

public class AuthClient {

    @Step("Request token for {username}")
    public Response login(String username, String password) {

        return given()
                .spec(Specs.base())
                .body(new AuthRequest(username, password))
                .when()
                .post("/auth");

    }

    public String createToken() {

        return login(Config.USERNAME, Config.PASSWORD)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class)
                .getToken();

    }

}

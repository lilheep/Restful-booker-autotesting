package clients;

import config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class Specs {

    private Specs() {}

    public static RequestSpecification base() {

        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .build();

    }

}

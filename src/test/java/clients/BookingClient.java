package clients;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Booking;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingClient {

    private RequestSpecification spec(String token) {

        RequestSpecification spec = given().spec(Specs.base());
        if (token != null) {
            spec.cookie("token", token);
        }
        return spec;

    }

    @Step("Create booking")
    public Response create(Booking booking) {

        return spec(null)
                .body(booking)
                .when()
                .post("/booking");

    }

    @Step("Get booking by id {id}")
    public Response getById(int id) {

        return spec(null)
                .when()
                .get("/booking/{id}", id);

    }

    @Step("Get booking id`s")
    public Response getIds(Map<String, Object> filters) {

        return spec(null)
                .queryParams(filters)
                .when()
                .get("/booking");

    }

    @Step("Update booking {id}")
    public Response update(int id, String token, Booking booking) {

        return spec(token)
                .body(booking)
                .when()
                .put("/booking/{id}", id);

    }

    @Step("Update booking {id} with basic auth")
    public Response updateWithAuth(int id, Booking booking) {

        return spec(null)
                .auth()
                .preemptive().basic(Config.USERNAME, Config.PASSWORD)
                .body(booking)
                .when()
                .put("/booking/{id}", id);

    }

    @Step("Partially update booking {id}")
    public Response patch(int id, String token, Booking booking) {

        return spec(token)
                .body(booking)
                .when()
                .patch("/booking/{id}", id);

    }

    @Step("Delete booking {id}")
    public Response delete(int id, String token) {

        return spec(token)
                .when()
                .delete("/booking/{id}", id);

    }

}

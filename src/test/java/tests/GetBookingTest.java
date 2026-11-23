package tests;

import data.BookingFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.Booking;
import models.CreatedBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@Epic("Restful-booking")
@Feature("Get booking")
public class GetBookingTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("Booking by id matches the schema and the created data")
    void getByIdReturnsBooking() {

        Booking request = BookingFactory.random();
        CreatedBooking created = createBooking(request);

        Response response = bookings.getById(created.getBookingId());
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"));

        assertThat(request).isEqualTo(response.as(Booking.class));

    }

    @Test
    @DisplayName("Non-existent booking returns 404")
    void nonExistentBookingReturnsError() {

        bookings.getById(Integer.MAX_VALUE)
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("Booking list matches the schema and contains the created booking")
    void bookingListContainsCreatedBooking() {

        CreatedBooking created = createBooking(BookingFactory.random());

        bookings.getIds(Map.of())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids.json"))
                .body("bookingid", hasItem(created.getBookingId()));

    }

    @Test
    @DisplayName("Filter by first name finds the created booking")
    void filterByFirstName() {

        Booking request = BookingFactory.random();
        CreatedBooking created = createBooking(request);

        bookings.getIds(Map.of("firstname", request.getFirstname()))
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids.json"))
                .body("bookingid", hasItem(created.getBookingId()));

    }

    @Test
    @DisplayName("Filter by last name finds the created booking")
    void filterByLastName() {

        Booking request = BookingFactory.random();
        CreatedBooking created = createBooking(request);

        bookings.getIds(Map.of("lastname", request.getLastname()))
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids.json"))
                .body("bookingid", hasItem(created.getBookingId()));

    }

    @Test
    @DisplayName("Filter by a name that does not exist returns an empty list")
    void filterByUnknownNameReturnsEmptyList() {

        bookings.getIds(Map.of("firstname", BookingFactory.randomLetters(30)))
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

    }

}

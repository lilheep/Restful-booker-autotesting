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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful-booking")
@Feature("Update booking")
public class UpdateBookingTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("PUT with token replaces the booking")
    void putWithTokenReplacesBooking() {

        CreatedBooking created = createBooking(BookingFactory.random());
        Booking updated = BookingFactory.random();

        Response response = bookings.update(created.getBookingId(), token, updated);
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"));

        assertThat(response.as(Booking.class)).isEqualTo(updated);

        Booking stored = bookings.getById(created.getBookingId())
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(stored).isEqualTo(updated);

    }

    @Test
    @DisplayName("PUT with basic auth replaces the booking")
    void putWithBasicAuthReplacesBooking() {

        CreatedBooking created = createBooking(BookingFactory.random());
        Booking updated = BookingFactory.random();

        Booking result = bookings.updateWithAuth(created.getBookingId(), updated)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(result).isEqualTo(updated);

    }

    @Test
    @DisplayName("PUT without authorization is forbidden and data stays unchanged")
    void putWithoutAuthIsForbidden() {

        Booking original = BookingFactory.random();
        CreatedBooking createdBooking = createBooking(original);

        bookings.update(createdBooking.getBookingId(), null, BookingFactory.random())
                .then()
                .statusCode(403);

        Booking stored = bookings.getById(createdBooking.getBookingId())
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(stored).isEqualTo(original);

    }

    @Test
    @DisplayName("PUT with an invalid token is forbidden")
    void putWithInvalidTokenIsForbidden() {
        CreatedBooking created = createBooking(BookingFactory.random());

        bookings.update(created.getBookingId(), "invalid-token", BookingFactory.random())
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("PATCH changes only the sent fields")
    void patchChangesOnlySentFields() {
        Booking original = BookingFactory.random();
        CreatedBooking created = createBooking(original);
        Booking patch = Booking.builder()
                .firstname("Patched")
                .additionalNeeds("Late checkout")
                .build();

        Response response = bookings.patch(created.getBookingId(), token, patch);
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"));

        Booking expected = original.toBuilder()
                .firstname("Patched")
                .additionalNeeds("Late checkout")
                .build();
        assertThat(response.as(Booking.class)).isEqualTo(expected);
    }

    @Test
    @DisplayName("PATCH without authorization is forbidden")
    void patchWithoutAuthIsForbidden() {
        CreatedBooking created = createBooking(BookingFactory.random());

        bookings.patch(created.getBookingId(), null,
                        Booking.builder().firstname("Hacker").build())
                .then()
                .statusCode(403);
    }

    @Test
    @Tag("known-issue")
    @DisplayName("PUT for a non-existent booking returns 405")
    void putForNonExistentBookingReturnsError() {
        bookings.update(Integer.MAX_VALUE, token, BookingFactory.random())
                .then()
                .statusCode(405);
    }

}

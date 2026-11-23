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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful-booking")
@Feature("Create booking")
public class CreateBookingTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("Created booking matches the schema and the sent data")
    void createBookingReturnsSentData() {

        Booking request = BookingFactory.random();
        Response response = bookings.create(request);

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response.json"));

        CreatedBooking createdBooking = response.as(CreatedBooking.class);
        track(createdBooking.getBookingId());

        assertThat(createdBooking.getBooking()).isEqualTo(request);

    }

    @Test
    @DisplayName("Created booking can be read back with identical data")
    void createdBookingIsPersisted() {

        Booking request = BookingFactory.random();
        CreatedBooking created = createBooking(request);

        Booking stored = bookings.getById(created.getBookingId())
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(stored).isEqualTo(request);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validBookings")
    @DisplayName("Valid data variations are saved as sent")
    void validVariationsAreSaved(String caseName, Booking request) {

        CreatedBooking createdBooking = createBooking(request);
        assertThat(createdBooking.getBooking()).isEqualTo(request);

    }

    static Stream<Arguments> validBookings() {

        Booking base = BookingFactory.random();
        return Stream.of(

                Arguments.of("deposit not paid", base.toBuilder().depositPaid(false).build()),
                Arguments.of("deposit paid", base.toBuilder().depositPaid(true).build()),
                Arguments.of("without additional needs", base.toBuilder().additionalNeeds(null).build()),
                Arguments.of("zero total price", base.toBuilder().totalPrice(0).build()),
                Arguments.of("long first name", base.toBuilder().firstname("A".repeat(100)).build()),
                Arguments.of("apostrophe and hyphen in name", base.toBuilder()
                        .firstname("O'Brien-Smith").build())

        );

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidBookings")
    @Tag("known-issue")
    @DisplayName("Booking with a missing required field is rejected")
    void missingRequiredFieldRejected(String caseName, Booking request) {

        bookings.create(request)
                .then()
                .statusCode(500);

    }

    static Stream<Arguments> invalidBookings() {

        Booking base = BookingFactory.random();
        return Stream.of(

                Arguments.of("no firstname", base.toBuilder().firstname(null).build()),
                Arguments.of("no lastname", base.toBuilder().lastname(null).build()),
                Arguments.of("no total price", base.toBuilder().totalPrice(null).build()),
                Arguments.of("no deposit paid", base.toBuilder().depositPaid(null).build()),
                Arguments.of("no booking date", base.toBuilder().bookingDates(null).build())

        );

    }
}

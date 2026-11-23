package tests;

import clients.AuthClient;
import clients.BookingClient;
import io.restassured.RestAssured;
import models.Booking;
import models.CreatedBooking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

abstract class BaseTest {

    protected static final BookingClient bookings = new BookingClient();
    protected static String token;

    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeAll
    static void init() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        if (token == null) {
            token = new AuthClient().createToken();
        }

    }

    @AfterEach
    void cleanUp() {

        createdIds.forEach(id -> bookings.delete(id, token));
        createdIds.clear();

    }

    protected void track(int bookingId) {

        createdIds.add(bookingId);

    }

    protected CreatedBooking createBooking(Booking booking) {

        CreatedBooking createdBooking = bookings.create(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(CreatedBooking.class);

        track(createdBooking.getBookingId());
        return createdBooking;

    }

}

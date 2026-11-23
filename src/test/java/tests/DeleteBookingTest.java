package tests;

import data.BookingFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.CreatedBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("Restful-booking")
@Feature("Delete booking")
public class DeleteBookingTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("DELETE with token removes the booking")
    void deleteWithTokenRemovesBooking() {

        CreatedBooking createdBooking = createBooking(BookingFactory.random());

        bookings.delete(createdBooking.getBookingId(), token)
                .then()
                .statusCode(201);

        bookings.getById(createdBooking.getBookingId())
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("DELETE without authorization is forbidden and the booking remains")
    void deleteWithoutAuthIsForbidden() {

        CreatedBooking createdBooking = createBooking(BookingFactory.random());

        bookings.delete(createdBooking.getBookingId(), null)
                .then()
                .statusCode(403);

        bookings.getById(createdBooking.getBookingId())
                .then()
                .statusCode(200);

    }

    @Test
    @Tag("known-issue")
    @DisplayName("Deleting the same booking twice returns 405")
    void deletingTwiceReturnsError() {

        CreatedBooking created = createBooking(BookingFactory.random());

        bookings.delete(created.getBookingId(), token)
                .then()
                .statusCode(201);

        bookings.delete(created.getBookingId(), token)
                .then()
                .statusCode(405);

    }

    @Test
    @Tag("known-issue")
    @DisplayName("Deleting a non-existent booking returns 405")
    void deletingNonExistentReturnsError() {
        bookings.delete(Integer.MAX_VALUE, token)
                .then()
                .statusCode(405);
    }

}

package tests;

import data.BookingFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.Booking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful-booking")
@Feature("Booking lifecycle")
public class BookingLifecycleTest extends BaseTest {

    @Test
    @Tag("e2e")
    @DisplayName("Create, read, update, patch and delete a booking")
    void fullLifecycle() {

        Booking original = BookingFactory.random();

        int id = Allure.step("Create booking", () ->
                createBooking(original).getBookingId());

        Allure.step("Read booking and compare", () -> {

            Booking stored = bookings.getById(id)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Booking.class);

            assertThat(stored).isEqualTo(original);

        });

        Booking replacement = BookingFactory.random();
        Allure.step("Replace booking with PUT", () -> {

            Booking result = bookings.update(id, token, replacement)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Booking.class);

            assertThat(result).isEqualTo(replacement);

        });

        Allure.step("Change price with PATCH", () -> {

            Booking result = bookings.patch(id, token, Booking.builder().totalPrice(777).build())
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Booking.class);

            assertThat(result).isEqualTo(replacement.toBuilder().totalPrice(777).build());

        });

        Allure.step("Delete booking", () ->
                bookings.delete(id, token).then().statusCode(201));

        Allure.step("Deleted booking is gone", () ->
                bookings.getById(id).then().statusCode(404));
    }

}



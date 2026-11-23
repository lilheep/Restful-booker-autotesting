package data;

import models.Booking;
import models.BookingDates;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public final class BookingFactory {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private BookingFactory() {}

    public static Booking random() {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDate checkIn = LocalDate.now().plusDays(random.nextInt(1, 100));

        return Booking.builder()
                .firstname(capitalize(randomLetters(8)))
                .lastname(capitalize(randomLetters(10)))
                .totalPrice(random.nextInt(50, 1000))
                .depositPaid(random.nextBoolean())
                .bookingDates(BookingDates.builder()
                        .checkin(checkIn.toString())
                        .checkout(checkIn.plusDays(random.nextInt(1, 14)).toString())
                        .build())
                .additionalNeeds("Breakfast")
                .build();

    }

    public static String randomLetters(int length) {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }

        return sb.toString();

    }

    public static String capitalize(String str) {

        if (str == null || str.isBlank()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);

    }

}

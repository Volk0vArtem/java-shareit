package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    EasyRandom generator = new EasyRandom();
    BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void toBooking() {
        BookingDto bookingDto = generator.nextObject(BookingDto.class);
        Booking booking = bookingMapper.toBooking(bookingDto);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingDto() {
        Booking booking = generator.nextObject(Booking.class);
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getId(), bookingDto.getId());
    }
}
package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookings(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);
}

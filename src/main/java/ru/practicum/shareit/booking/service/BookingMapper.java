package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBooking (BookingDto bookingDto);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    BookingDto toBookingDto (Booking booking);
}

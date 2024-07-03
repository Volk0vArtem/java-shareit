package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    UserDto user1;
    UserDto user2;
    ItemDto item;
    BookingDto booking;
    EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        user1 = generator.nextObject(UserDto.class);
        user2 = generator.nextObject(UserDto.class);
        user1 = userService.saveUser(user1);
        user2 = userService.saveUser(user2);
        item = generator.nextObject(ItemDto.class);
        item = itemService.saveItem(item, user1.getId());
        booking = new BookingDto();
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setItemId(item.getId());
    }

    @Test
    void save() {
        BookingDto savedBooking = bookingService.save(booking, user2.getId());
        savedBooking = bookingService.getBooking(savedBooking.getId(), user1.getId());

        assertEquals(booking.getStart(), savedBooking.getStart());
        assertEquals(booking.getEnd(), savedBooking.getEnd());
        assertEquals(booking.getItemId(), savedBooking.getItemId());
        assertEquals(user2.getId(), savedBooking.getBooker().getId());
        assertEquals(Status.WAITING, savedBooking.getStatus());
    }

    @Test
    void approve() {
        BookingDto savedBooking = bookingService.save(booking, user2.getId());
        bookingService.approve(savedBooking.getId(), user1.getId(), true);
        BookingDto approvedBooking = bookingService.getBooking(savedBooking.getId(), user1.getId());

        assertEquals(Status.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void getBookings() {
        BookingDto booking2 = new BookingDto();
        booking2.setStart(LocalDateTime.now().plusSeconds(10));
        booking2.setEnd(LocalDateTime.now().plusSeconds(15));
        booking2.setItemId(item.getId());
        booking = bookingService.save(booking, user2.getId());
        booking2 = bookingService.save(booking2, user2.getId());

        List<BookingDto> bookings = bookingService.getBookings(user2.getId(), "ALL");

        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(1).getId());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    void getBookingsByOwner() {
        BookingDto booking2 = new BookingDto();
        booking2.setStart(LocalDateTime.now().plusSeconds(10));
        booking2.setEnd(LocalDateTime.now().plusSeconds(15));
        booking2.setItemId(item.getId());
        booking = bookingService.save(booking, user2.getId());
        booking2 = bookingService.save(booking2, user2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user1.getId(), "ALL");

        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(1).getId());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }
}
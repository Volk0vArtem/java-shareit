package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User user1;
    User user2;
    Item item;
    Booking booking1;
    Booking booking2;


    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@gmail.com");

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@gmail.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user1);

        item = itemRepository.save(item);

        booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(user2);
        booking1.setStatus(Status.WAITING);

        booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);
    }

    @Test
    void findCurrentBookings() {
        booking1.setStart(LocalDateTime.now().minusMinutes(10));
        booking1.setEnd(LocalDateTime.now().plusMinutes(10));
        booking1 = bookingRepository.save(booking1);

        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(20));
        booking2 = bookingRepository.save(booking2);

        List<Booking> result = bookingRepository.findCurrentBookings(user2.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(booking1));
    }

    @Test
    void findCurrentBookingsByOwner() {
        booking1.setStart(LocalDateTime.now().minusMinutes(10));
        booking1.setEnd(LocalDateTime.now().plusMinutes(10));
        booking1 = bookingRepository.save(booking1);

        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(20));
        booking2 = bookingRepository.save(booking2);

        List<Booking> result = bookingRepository.findCurrentBookingsByOwner(user1.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(booking1));
    }

    @Test
    void findPastBookings() {
        booking1.setStart(LocalDateTime.now().minusMinutes(10));
        booking1.setEnd(LocalDateTime.now().minusMinutes(5));
        booking1 = bookingRepository.save(booking1);

        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(20));
        booking2 = bookingRepository.save(booking2);

        List<Booking> result = bookingRepository.findPastBookings(item.getId(), user1.getId(), LocalDateTime.now());

        assertEquals(1, result.size());
        assertTrue(result.contains(booking1));
    }

    @Test
    void findFutureBookings() {
        booking1.setStart(LocalDateTime.now().minusMinutes(10));
        booking1.setEnd(LocalDateTime.now().minusMinutes(5));
        booking1 = bookingRepository.save(booking1);

        booking2.setStart(LocalDateTime.now().plusMinutes(10));
        booking2.setEnd(LocalDateTime.now().plusMinutes(20));
        booking2 = bookingRepository.save(booking2);

        List<Booking> result = bookingRepository.findFutureBookings(item.getId(), user1.getId(), LocalDateTime.now());

        assertEquals(1, result.size());
        assertTrue(result.contains(booking2));
    }
}
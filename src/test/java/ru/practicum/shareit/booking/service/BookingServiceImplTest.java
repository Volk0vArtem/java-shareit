package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final EasyRandom generator = new EasyRandom();
    Item item;
    User owner;
    User booker;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingMapper, bookingRepository, itemRepository, userRepository);
        owner = generator.nextObject(User.class);
        item = generator.nextObject(Item.class);
        item.setOwner(owner);
        booker = generator.nextObject(User.class);
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 10, 1, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 10, 2, 0, 0));
        bookingDto.setItemId(item.getId());
        bookingDto.setBookerId(booker.getId());
    }

    @Test
    void save() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingMapper.toBooking(bookingDto));

        bookingService.save(bookingDto, booker.getId());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void saveBookerNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, booker.getId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookerIsOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, owner.getId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDto, booker.getId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveItemNotAvailable() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        item.setAvailable(false);
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.save(bookingDto, booker.getId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveItemWrongTime() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        bookingDto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.save(bookingDto, booker.getId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approve() {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.getReferenceById(booking.getId()))
                .thenReturn(booking);

        bookingService.approve(booking.getId(), owner.getId(), true);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approveFailStatus() {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.getReferenceById(booking.getId()))
                .thenReturn(booking);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approve(booking.getId(), owner.getId(), true));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveFailNotOwner() {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.getReferenceById(booking.getId()))
                .thenReturn(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(booking.getId(), booker.getId(), true));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBooking() {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        bookingService.getBooking(booking.getId(), owner.getId());
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getBookingWrongUser() {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), owner.getId() + 1));
    }

    @Test
    void getBookingsAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        bookingService.getBookings(owner.getId(), "ALL");

        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(owner.getId());
    }

    @Test
    void getBookingsCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookings(owner.getId(), "CURRENT");

        verify(bookingRepository).findCurrentBookings(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookings(owner.getId(), "PAST");

        verify(bookingRepository).findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookings(owner.getId(), "FUTURE");

        verify(bookingRepository).findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookings(owner.getId(), "WAITING");

        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING);
    }

    @Test
    void getBookingsRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookings(owner.getId(), "REJECTED");

        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED);
    }

    @Test
    void getBookingsWrongStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookings(owner.getId(), "REJECT"));
    }

    @Test
    void getBookingsByOwnerAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwner(owner.getId(), "ALL");

        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    void getBookingsByOwnerCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookingsByOwner(owner.getId(), "CURRENT");

        verify(bookingRepository).findCurrentBookingsByOwner(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwnerPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookingsByOwner(owner.getId(), "PAST");

        verify(bookingRepository).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwnerFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookingsByOwner(owner.getId(), "FUTURE");

        verify(bookingRepository).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwnerWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookingsByOwner(owner.getId(), "WAITING");

        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING);
    }

    @Test
    void getBookingsByOwnerRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        bookingService.getBookingsByOwner(owner.getId(), "REJECTED");

        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED);
    }

    @Test
    void getBookingsByOwnerWrongStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByOwner(owner.getId(), "REJECT"));
    }
}
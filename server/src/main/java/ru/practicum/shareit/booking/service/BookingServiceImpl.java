package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto save(BookingDto bookingDto, Long userId) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        booking.setItem(item);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        booking.setBooker(user);
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Ошибка бронирования");
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь занята");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Ошибочное время бронирования");
        }
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalArgumentException("Невозможно изменить статус завершенного бронирования");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Одобрить бронирование может только владелец вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("У пользователя нет доступа к этому бронированию");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, String state, PageRequest pageRequest) {
        checkUser(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentBookings(userId, LocalDateTime.now(), pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state, PageRequest pageRequest) {
        checkUser(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByOwner(userId, LocalDateTime.now(), pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state),
                                pageRequest)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private Booking checkBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private void checkUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}

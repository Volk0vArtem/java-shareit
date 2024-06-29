package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping
    public ResponseEntity<BookingDto> save(@RequestBody @Valid BookingDto bookingDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление бронирования {}", bookingDto);
        return ResponseEntity.ok().body(service.save(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam Boolean approved) {
        log.info("Получен запрос на {} бронирования", approved);
        return ResponseEntity.ok().body(service.approve(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получении данных о бронировании id={} от пользователя userId={}", bookingId, userId);
        return ResponseEntity.ok().body(service.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение бронирований пользователя id={}, state={}", userId, state);
        return ResponseEntity.ok().body(service.getBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение бронирований вещей пользователя id={}, state={}", userId, state);
        return ResponseEntity.ok().body(service.getBookingsByOwner(userId, state));
    }
}

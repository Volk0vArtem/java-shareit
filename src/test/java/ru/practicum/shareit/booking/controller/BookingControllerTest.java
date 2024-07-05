package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingServiceImpl bookingService;

    BookingDto booking;

    @BeforeEach
    void setUp() {
        booking = new BookingDto();
        booking.setStart(LocalDateTime.of(2025, 10, 10, 12, 0, 0));
        booking.setEnd(LocalDateTime.of(2025, 10, 11, 12, 0, 0));
        booking.setItemId(1L);
    }

    @Test
    void save() throws Exception {
        when(bookingService.save(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingDto = invocationOnMock.getArgument(0, BookingDto.class);
                    bookingDto.setId(1L);
                    return bookingDto;
                });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start").value((booking.getStart().format(formatter))))
                .andExpect(jsonPath("$.end").value((booking.getEnd().format(formatter))))
                .andExpect(jsonPath("$.itemId", is(booking.getItemId()), Long.class));
    }

    @Test
    void saveStartFail() throws Exception {
        when(bookingService.save(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingDto = invocationOnMock.getArgument(0, BookingDto.class);
                    bookingDto.setId(1L);
                    return bookingDto;
                });
        booking.setStart(LocalDateTime.now().minusSeconds(100));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveEndFail() throws Exception {
        when(bookingService.save(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingDto = invocationOnMock.getArgument(0, BookingDto.class);
                    bookingDto.setId(1L);
                    return bookingDto;
                });
        booking.setEnd(LocalDateTime.now().minusSeconds(100));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveHeaderFail() throws Exception {
        when(bookingService.save(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingDto = invocationOnMock.getArgument(0, BookingDto.class);
                    bookingDto.setId(1L);
                    return bookingDto;
                });
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
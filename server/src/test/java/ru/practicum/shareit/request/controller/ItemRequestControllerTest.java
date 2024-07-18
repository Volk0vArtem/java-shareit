package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService service;
    private ItemRequestDto itemRequest1;
    private ItemRequestDto itemRequest2;

    @BeforeEach
    void setUp() {
        itemRequest1 = new ItemRequestDto();
        itemRequest1.setRequesterId(1L);
        itemRequest1.setDescription("description1");
        itemRequest1.setCreated(LocalDateTime.of(2024, 10, 10, 12, 0, 0));

        itemRequest2 = new ItemRequestDto();
        itemRequest2.setRequesterId(1L);
        itemRequest2.setDescription("description2");
        itemRequest2.setCreated(LocalDateTime.of(2025, 10, 10, 12, 0, 0));
    }

    @Test
    void save() throws Exception {
        when(service.save(any(ItemRequestDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequestDto = invocationOnMock.getArgument(0, ItemRequestDto.class);
                    itemRequestDto.setId(1L);

                    return itemRequestDto;
                });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.created").value((itemRequest1.getCreated().format(formatter))))
                .andExpect(jsonPath("$.description", is(itemRequest1.getDescription())));
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        mvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).getAll(anyLong(), any(PageRequest.class));
    }
}
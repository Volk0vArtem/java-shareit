package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {
    ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    EasyRandom generator = new EasyRandom();

    @Test
    void toItemRequest() {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }
}
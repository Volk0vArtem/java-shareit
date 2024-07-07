package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;

    private UserDto user1;
    private UserDto user2;
    private ItemRequestDto itemRequest;

    @BeforeEach
    void setUp() {
        user1 = new UserDto(null, "user1", "user1@gmail.com");
        user2 = new UserDto(null, "user2", "user2@gmail.com");
        user1 = userService.saveUser(user1);
        user2 = userService.saveUser(user2);
        itemRequest = new ItemRequestDto();
        itemRequest.setDescription("description");
    }

    @Test
    void save() {
        itemRequestService.save(itemRequest, user2.getId());
        ItemRequestDto savedRequest = itemRequestService.getAll(user1.getId(), PageRequest.of(0, 1)).get(0);

        itemRequest.setRequesterId(user2.getId());
        assertEquals(itemRequest.getRequesterId(), user2.getId());
        assertEquals(itemRequest.getDescription(), savedRequest.getDescription());
    }

    @Test
    void getRequestsByRequester() {
        itemRequest = itemRequestService.save(itemRequest, user2.getId());
        List<ItemRequestDto> savedRequests = itemRequestService.getRequestsByRequester(user2.getId());

        assertEquals(itemRequest.getRequesterId(), user2.getId());
        assertTrue(savedRequests.contains(itemRequest));
    }

    @Test
    void getAll() {
        ItemRequestDto itemRequest2 = new ItemRequestDto();
        itemRequest2.setDescription("description2");
        itemRequest = itemRequestService.save(itemRequest, user2.getId());
        itemRequestService.save(itemRequest2, user1.getId());
        List<ItemRequestDto> result = itemRequestService.getAll(user1.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(itemRequest));
    }
}







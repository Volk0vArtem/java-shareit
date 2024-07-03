package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BookingService bookingService;
    private final EasyRandom generator = new EasyRandom();
    private UserDto user1;
    private ItemDto item1;
    private ItemDto item2;

    @BeforeEach
    void setUp() {
        user1 = generator.nextObject(UserDto.class);
        item1 = generator.nextObject(ItemDto.class);
        item2 = generator.nextObject(ItemDto.class);
    }

    @Test
    void saveItem() {
        user1 = userService.saveUser(user1);
        ItemDto savedItem = itemService.saveItem(item1, user1.getId());
        ItemDto loadedItem = itemService.getItem(savedItem.getId(), user1.getId());

        assertEquals(item1.getName(), loadedItem.getName());
        assertEquals(item1.getDescription(), loadedItem.getDescription());
        assertEquals(item1.getAvailable(), loadedItem.getAvailable());
        assertEquals(true, item1.getAvailable());
    }


    @Test
    void getItemsByID() {
        user1 = userService.saveUser(user1);
        itemService.saveItem(item1, user1.getId());
        itemService.saveItem(item2, user1.getId());
        List<ItemDto> items = itemService.getItemsByID(user1.getId());

        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
        assertEquals(item1.getAvailable(), items.get(0).getAvailable());

        assertEquals(item2.getName(), items.get(1).getName());
        assertEquals(item2.getDescription(), items.get(1).getDescription());
        assertEquals(item2.getAvailable(), items.get(1).getAvailable());
    }

    @Test
    void patchItem() {
        user1 = userService.saveUser(user1);
        item1 = itemService.saveItem(item1, user1.getId());
        ItemDto patchedItem = itemService.patchItem(item2, item1.getId(), user1.getId());

        assertEquals(item2.getName(), patchedItem.getName());
        assertEquals(item2.getDescription(), patchedItem.getDescription());
        assertEquals(item2.getAvailable(), patchedItem.getAvailable());
    }

    @Test
    void search() {
        user1 = userService.saveUser(user1);
        itemService.saveItem(item1, user1.getId());
        item2.setName("текст");
        itemService.saveItem(item2, user1.getId());
        List<ItemDto> search = itemService.search("тек");

        assertEquals(1, search.size());
        assertEquals(item2.getName(), search.get(0).getName());
        assertEquals(item2.getDescription(), search.get(0).getDescription());
        assertEquals(item2.getAvailable(), search.get(0).getAvailable());
    }

    @Test
    void postComment() throws InterruptedException {
        UserDto user2 = generator.nextObject(UserDto.class);
        user1 = userService.saveUser(user1);
        user2 = userService.saveUser(user2);
        item1 = itemService.saveItem(item1, user1.getId());

        BookingDto booking = new BookingDto();
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setItemId(item1.getId());

        booking = bookingService.save(booking, user2.getId());

        bookingService.approve(booking.getId(), user1.getId(), true);

        Thread.sleep(3000);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        commentDto.setAuthorId(user2.getId());
        itemService.postComment(commentDto, item1.getId(), user2.getId());

        item1 = itemService.getItem(item1.getId(), user1.getId());

        assertEquals(1, item1.getComments().size());
        assertEquals(commentDto.getText(), item1.getComments().get(0).getText());
        assertEquals(user2.getName(), item1.getComments().get(0).getAuthorName());
        assertEquals(item1.getName(), item1.getComments().get(0).getItem().getName());
    }
}
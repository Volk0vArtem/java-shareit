package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user1;
    Item item1;
    Item item2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@gmail.com");

        item1 = new Item();
        item1.setName("item1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setName("item2");
        item2.setDescription("description2");
        item2.setAvailable(true);
        item2.setOwner(user1);

        user1 = userRepository.save(user1);
        itemRepository.save(item1);
        itemRepository.save(item2);

    }

    @Test
    public void testSearchByName() {
        List<Item> result = itemRepository.search("item1", PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    public void testSearchByDescription() {
        List<Item> result = itemRepository.search("description1", PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(item1));
    }

    @Test
    public void testSearchSubstring() {
        List<Item> result = itemRepository.search("tion2", PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(item2));
    }

    @Test
    public void testSearchTwoResults() {
        List<Item> result = itemRepository.search("description", PageRequest.of(0, 10));

        assertEquals(2, result.size());
        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));
    }

    @Test
    public void testSearchNotFound() {
        List<Item> result = itemRepository.search("text", PageRequest.of(0, 10));

        assertEquals(0, result.size());
    }

    @Test
    public void testFindAllByOwnerID() {
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@gmail.com");

        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("description3");
        item3.setAvailable(true);
        item3.setOwner(user2);

        user2 = userRepository.save(user2);
        itemRepository.save(item3);

        List<Item> result = itemRepository.findAllByOwnerIdOrderByIdAsc(user2.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertTrue(result.contains(item3));
    }

    @Test
    public void testFindAllByOwnerIDTwoResults() {
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@gmail.com");

        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("description3");
        item3.setAvailable(true);
        item3.setOwner(user2);

        userRepository.save(user2);
        itemRepository.save(item3);

        List<Item> result = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), PageRequest.of(0, 10));

        assertEquals(2, result.size());
        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));
    }

    @Test
    public void testFindAllByOwnerIDNoResults() {
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@gmail.com");

        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("description3");
        item3.setAvailable(true);
        item3.setOwner(user2);

        userRepository.save(user2);
        itemRepository.save(item3);

        List<Item> result = itemRepository.findAllByOwnerIdOrderByIdAsc(999L, PageRequest.of(0, 10));

        assertEquals(0, result.size());
    }
}
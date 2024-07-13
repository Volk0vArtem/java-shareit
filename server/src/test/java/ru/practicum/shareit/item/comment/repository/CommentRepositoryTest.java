package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    Comment comment1;
    Comment comment2;
    User user1;
    User user2;
    Item item1;
    Item item2;


    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@gmail.com");

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@gmail.com");

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
        user2 = userRepository.save(user2);
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        comment1 = new Comment();
        comment1.setCreated(LocalDateTime.now());
        comment1.setAuthor(user2);
        comment1.setText("comment1");
        comment1.setItem(item1);

        comment2 = new Comment();
        comment2.setCreated(LocalDateTime.now());
        comment2.setAuthor(user2);
        comment2.setText("comment2");
        comment2.setItem(item2);


    }

    @Test
    void getAllByItemId() {
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);

        List<Comment> result = commentRepository.getAllByItemId(comment1.getItem().getId());
        assertEquals(1, result.size());
        assertTrue(result.contains(comment1));
        //assertTrue(result.contains(comment2));
    }

    @Test
    void getAllByItemIdTwoComments() {
        comment1.setItem(item2);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);

        List<Comment> result = commentRepository.getAllByItemId(comment1.getItem().getId());
        assertEquals(2, result.size());
        assertTrue(result.contains(comment1));
        assertTrue(result.contains(comment2));
    }

    @Test
    void getAllByItemIdNoResults() {
        comment1.setItem(item2);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);

        List<Comment> result = commentRepository.getAllByItemId(999L);
        assertEquals(0, result.size());
    }
}
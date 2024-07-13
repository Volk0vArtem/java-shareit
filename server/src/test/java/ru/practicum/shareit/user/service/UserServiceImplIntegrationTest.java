package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;
    private final EasyRandom generator = new EasyRandom();
    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = generator.nextObject(UserDto.class);
        user2 = generator.nextObject(UserDto.class);
    }

    @Test
    void saveUser() {
        UserDto savedUser = userService.saveUser(user1);

        assertEquals(user1.getName(), savedUser.getName());
        assertEquals(user1.getEmail(), savedUser.getEmail());
    }

    @Test
    void getUsers() {
        userService.saveUser(user1);
        userService.saveUser(user2);
        List<UserDto> users = userService.getUsers();

        assertEquals(user1.getName(), users.get(0).getName());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
        assertEquals(user2.getName(), users.get(1).getName());
        assertEquals(user2.getEmail(), users.get(1).getEmail());
    }

    @Test
    void patchUser() {
        user1 = userService.saveUser(user1);
        userService.patchUser(user2, user1.getId());
        UserDto patchedUser = userService.getUser(user1.getId());

        assertEquals(user2.getName(), patchedUser.getName());
        assertEquals(user2.getEmail(), patchedUser.getEmail());
    }

    @Test
    void deleteUser() {
        user1 = userService.saveUser(user1);
        userService.saveUser(user2);
        userService.deleteUser(user1.getId());

        List<UserDto> users = userService.getUsers();

        assertEquals(1, users.size());
        assertEquals(user2.getName(), users.get(0).getName());
        assertEquals(user2.getEmail(), users.get(0).getEmail());
    }
}
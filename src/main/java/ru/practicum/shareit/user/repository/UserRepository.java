package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User getUser(Long id);

    List<User> getUsers();

    User patchUser(UserDto userDto, Long id);

    void deleteUser(Long id);
}

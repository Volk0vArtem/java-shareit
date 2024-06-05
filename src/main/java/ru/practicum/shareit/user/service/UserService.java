package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    User getUser(Long id);

    List<User> getUsers();

    User patchUser(UserDto userDto, Long id);

    void deleteUser(Long id);
}

package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto getUser(Long id);

    List<UserDto> getUsers();

    UserDto patchUser(UserDto userDto, Long id);

    void deleteUser(Long id);
}

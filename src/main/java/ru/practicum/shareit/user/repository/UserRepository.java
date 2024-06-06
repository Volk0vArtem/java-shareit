package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User getUser(Long id);

    List<User> getUsers();

    User patchUser(User user, Long id);

    void deleteUser(Long id);
}

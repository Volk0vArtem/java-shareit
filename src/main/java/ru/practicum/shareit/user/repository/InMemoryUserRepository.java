package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private static Long idCount = 0L;

    @Override
    public User saveUser(User user) {
        for (User savedUser : users.values()) {
            if (savedUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже зарегистрирован");
            }
        }
        user.setId(++idCount);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User patchUser(UserDto userDto, Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            for (User savedUser : users.values()) {
                if (savedUser.getEmail().equals(userDto.getEmail())) {
                    throw new ValidationException("Пользователь с таким email уже зарегистрирован");
                }
            }
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        users.remove(id);
    }
}

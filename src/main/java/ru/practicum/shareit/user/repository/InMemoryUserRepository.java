package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
                throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            }
        }
        user.setId(++idCount);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User patchUser(User newUser, Long id) {
        User user = getUser(id);
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            for (User savedUser : users.values()) {
                if (savedUser.getEmail().equals(newUser.getEmail())) {
                    throw new ConflictException("Пользователь с таким email уже зарегистрирован");
                }
            }
            user.setEmail(newUser.getEmail());
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

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    public User saveUser(@RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на сохранение пользователя {}", userDto);
        return service.saveUser(userDto);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id={}", id);
        return service.getUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return service.getUsers();
    }

    @PatchMapping("/{id}")
    public User patchUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Получен запрос на изменение пользователя {}, id={}", userDto, id);
        return service.patchUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с id={}", id);
        service.deleteUser(id);
    }
}

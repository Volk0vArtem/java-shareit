package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private final UserMapper userMapper = new UserMapperImpl();
    private User user;
    private final EasyRandom generator = new EasyRandom();


    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        user = generator.nextObject(User.class);
    }

    @Test
    void getUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.getUser(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.getUser(1L));
    }

    @Test
    void patchUser() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(user.getId());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        userService.patchUser(userDto, userDto.getId());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void patchUserNotFound() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(user.getId());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.patchUser(userDto, userDto.getId()));

        verify(userRepository, never()).save(any(User.class));
    }
}
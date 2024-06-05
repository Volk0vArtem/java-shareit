package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User saveUser(UserDto userDto) {
        return userRepository.saveUser(modelMapper.map(userDto, User.class));
    }

    @Override
    public User getUser(Long id) {
        return userRepository.getUser(id);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User patchUser(UserDto userDto, Long id) {
        return userRepository.patchUser(userDto, id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}

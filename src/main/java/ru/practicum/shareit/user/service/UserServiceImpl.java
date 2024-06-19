package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserDto userDto) {
        return userMapper.toUserDto(userRepository.saveUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUser(Long id) {
        return userMapper.toUserDto(userRepository.getUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long id) {
        return userMapper.toUserDto(userRepository.patchUser(userMapper.toUser(userDto), id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}

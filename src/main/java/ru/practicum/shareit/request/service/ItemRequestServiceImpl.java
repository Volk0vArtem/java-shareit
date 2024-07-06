package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        itemRequest.setRequester(user.get());
        itemRequest.setCreated(LocalDateTime.now());
        return mapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequester(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, PageRequest pageRequest) {
        return itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest).stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);
        if (request.isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(request.get());
        itemRequestDto.setItems(request.get().getItems().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }
}

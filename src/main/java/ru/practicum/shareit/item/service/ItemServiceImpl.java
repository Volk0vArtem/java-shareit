package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        userRepository.getUser(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        return itemMapper.toItemDto(repository.saveItem(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(repository.getItem(id));
    }

    @Override
    public List<ItemDto> getItemsByID(Long id) {
        return repository.getItemsByID(id).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long id, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходим id пользователя");
        }
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(repository.patchItem(item, id, userId));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.search(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}

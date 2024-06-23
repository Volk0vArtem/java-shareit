package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        item.setAvailable(true);
        return itemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        Optional<Item> item = repository.findById(id);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        return itemMapper.toItemDto(item.get());
    }

    @Override
    public List<ItemDto> getItemsByID(Long id) {
        return repository.findAllByOwner(id).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long id, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходим id пользователя");
        }
        Item newItem = itemMapper.toItem(itemDto);
        Item item = repository.getReferenceById(id);
        if (!item.getOwner().equals(userId)) {
            throw new ForbiddenException("Пользователь не имеет доступа к этой вещи");
        }

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }

        return itemMapper.toItemDto(repository.save(item));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> result = repository.search(text);
        return result.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}

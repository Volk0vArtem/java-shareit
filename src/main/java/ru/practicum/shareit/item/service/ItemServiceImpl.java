package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Item saveItem(ItemDto itemDto, Long userId) {
        userRepository.getUser(userId);
        Item item = modelMapper.map(itemDto, Item.class);
        item.setOwner(userId);
        return repository.saveItem(item);
    }

    @Override
    public Item getItem(Long id) {
        return repository.getItem(id);
    }

    @Override
    public List<Item> getItemsByID(Long id) {
        return repository.getItemsByID(id);
    }

    @Override
    public Item patchItem(ItemDto itemDto, Long id, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходим id пользователя");
        }
        Item item = modelMapper.map(itemDto, Item.class);
        return repository.patchItem(item, id, userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Текст для поиска не может быть пустым");
        }
        return repository.search(text);
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        repository.deleteItem(id, userId);
    }
}

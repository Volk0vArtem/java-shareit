package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(ItemDto itemDto, Long userId);

    Item getItem(Long id);

    List<Item> getItemsByID(Long id);

    Item patchItem(ItemDto itemDto, Long id, Long userId);

    void deleteItem(Long id, Long userId);

    List<Item> search(String text);
}

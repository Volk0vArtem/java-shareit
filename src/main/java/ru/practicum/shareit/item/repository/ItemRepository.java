package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item saveItem(Item item);

    Item getItem(Long id);

    List<Item> getItemsByID(Long id);

    Item patchItem(Item item, Long id, Long userId);

    void deleteItem(Long id, Long userId);

    List<Item> search(String text);

}

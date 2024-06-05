package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    private static Long idCount = 0L;

    @Override
    public Item saveItem(Item item) {
        item.setId(++idCount);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByID(Long id) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Item patchItem(Item newItem, Long id, Long userId) {
        Item item = items.get(id);

        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Вещь не найдена");
        }

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        return item;
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        if (items.get(id).getOwner().equals(userId)) {
            items.remove(id);
        }
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }
}
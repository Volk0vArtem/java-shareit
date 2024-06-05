package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService service;

    @PostMapping
    public Item saveItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи {}", itemDto);
        return service.saveItem(itemDto, userId);
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        log.info("Получен запрос на получение вещи id={}", id);
        return service.getItem(id);
    }

    @GetMapping
    public List<Item> getItemsByID(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("Получен запрос на получение вещей пользователя id={}", id);
        return service.getItemsByID(id);
    }

    @PatchMapping("/{id}")
    public Item patchItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на изменение вещи id={}", id);
        return service.patchItem(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на удаление вещи id={}", id);
        service.deleteItem(id, userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи по запросу «{}»", text);
        return service.search(text);
    }

}

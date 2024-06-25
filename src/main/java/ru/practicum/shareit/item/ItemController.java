package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> saveItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи {}", itemDto);
        return ResponseEntity.ok().body(service.saveItem(itemDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи id={}", id);
        return ResponseEntity.ok().body(service.getItem(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByID(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("Получен запрос на получение вещей пользователя id={}", id);
        return ResponseEntity.ok().body(service.getItemsByID(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> patchItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на изменение вещи id={}", id);
        return ResponseEntity.ok().body(service.patchItem(itemDto, id, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи по запросу «{}»", text);
        return ResponseEntity.ok().body(service.search(text));
    }
}

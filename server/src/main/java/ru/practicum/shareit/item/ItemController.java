package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> saveItem(@RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи {}", itemDto);
        return ResponseEntity.ok().body(service.saveItem(itemDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи id={}", id);
        return ResponseEntity.ok().body(service.getItem(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByID(@RequestHeader("X-Sharer-User-Id") Long id,
                                                      @RequestParam int from,
                                                      @RequestParam int size) {
        log.info("Получен запрос на получение вещей пользователя id={}", id);
        return ResponseEntity.ok().body(service.getItemsByID(id, PageRequest.of(from / size, size)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> patchItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на изменение вещи id={}", id);
        return ResponseEntity.ok().body(service.patchItem(itemDto, id, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Некорректные параметры пагинации");
        }
        log.info("Получен запрос на поиск вещи по запросу «{}»", text);
        return ResponseEntity.ok().body(service.search(text, PageRequest.of(from / size, size)));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление комментария для вещи id={}, text='{}' от пользователя id={}",
                itemId, commentDto.getText(), userId);
        return ResponseEntity.ok().body(service.postComment(commentDto, itemId, userId));
    }
}

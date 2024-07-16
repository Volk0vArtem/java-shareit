package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemRequestDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи {}", itemDto);
        return itemClient.saveItem(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи id={}", id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsById(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение вещей пользователя id={}, from={}, size={}", id, from, size);
        return itemClient.getItemsById(id, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestBody ItemRequestDto itemDto, @PathVariable Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на изменение вещи id={}", id);
        return itemClient.patchItem(itemDto, id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Некорректные параметры пагинации");
        }
        log.info("Получен запрос на поиск вещи по запросу «{}»", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentRequestDto commentDto, @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление комментария для вещи id={}, text='{}' от пользователя id={}",
                itemId, commentDto.getText(), userId);
        return itemClient.postComment(commentDto, itemId, userId);
    }
}

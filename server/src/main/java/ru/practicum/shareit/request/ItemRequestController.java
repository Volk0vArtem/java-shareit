package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> save(@RequestBody ItemRequestDto itemRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на сохранение запроса");
        return ResponseEntity.ok().body(service.save(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequestsByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка запросов пользователя id={}", userId);
        return ResponseEntity.ok().body(service.getRequestsByRequester(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получен запрос на получение списка запросов");
        return ResponseEntity.ok().body(service.getAll(userId, PageRequest.of(from / size, size)));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequest(@PathVariable Long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запроса id={}", requestId);
        return ResponseEntity.ok().body(service.getRequest(requestId, userId));
    }
}

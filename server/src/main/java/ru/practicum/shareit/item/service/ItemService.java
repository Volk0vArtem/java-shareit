package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long id, Long userId);

    List<ItemDto> getItemsByID(Long id, PageRequest pageRequest);

    ItemDto patchItem(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> search(String text, PageRequest pageRequest);

    CommentDto postComment(CommentDto commentDto, Long itemId, Long userId);
}

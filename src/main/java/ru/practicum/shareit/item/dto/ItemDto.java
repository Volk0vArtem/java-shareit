package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequest itemRequest;
}

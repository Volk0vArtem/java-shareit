package ru.practicum.shareit.request.service;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}

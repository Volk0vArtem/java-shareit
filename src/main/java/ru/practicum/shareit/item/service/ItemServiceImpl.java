package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.getReferenceById(userId));
        item.setAvailable(true);
        return itemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto getItem(Long id, Long userId) {
        Optional<Item> itemOptional = repository.findById(id);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        ItemDto itemDto = itemMapper.toItemDto(itemOptional.get());
        if (itemDto.getId().equals(userId)) {
            itemDto.setLastBooking(bookingMapper.toBookingDto(getLastBooking(id, userId)));
            itemDto.setNextBooking(bookingMapper.toBookingDto(getNextBooking(id, userId)));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByID(Long id) {
        return repository.findAllByOwnerId(id).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long id, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходим id пользователя");
        }
        Item newItem = itemMapper.toItem(itemDto);
        Item item = repository.getReferenceById(id);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не имеет доступа к этой вещи");
        }

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }

        return itemMapper.toItemDto(repository.save(item));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> result = repository.search(text);
        return result.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    private Booking getLastBooking(Long itemId, Long userId) {
        List<Booking> pastBookings = bookingRepository.findPastBookings(itemId, userId, LocalDateTime.now());
        if (pastBookings.isEmpty()) {
            return null;
        }
        return pastBookings.get(0);
    }

    private Booking getNextBooking(Long itemId, Long userId) {
        List<Booking> nextBookings = bookingRepository.findFutureBookings(itemId, userId, LocalDateTime.now());
        if (nextBookings.isEmpty()) {
            return null;
        }
        return nextBookings.get(0);
    }
}

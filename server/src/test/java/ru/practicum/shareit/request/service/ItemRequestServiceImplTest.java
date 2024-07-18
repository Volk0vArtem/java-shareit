package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemMapperImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    private ItemRequestService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestDto itemRequestDto;
    private User user;


    @BeforeEach
    void setUp() {
        service = new ItemRequestServiceImpl(itemRequestMapper, itemMapper, userRepository, itemRequestRepository);
        user = new User(1L, "user", "user@gmail.com");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequesterId(user.getId());
        itemRequestDto.setDescription("description");
    }

    @Test
    void save() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequestMapper.toItemRequest(itemRequestDto));
        service.save(itemRequestDto, user.getId());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void saveUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.save(itemRequestDto, user.getId()));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsByRequester() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequestMapper.toItemRequest(itemRequestDto)));
        service.getRequestsByRequester(user.getId());
        verify(itemRequestRepository).findAllByRequesterId(anyLong());
    }

    @Test
    void getRequestsByRequesterUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRequestsByRequester(user.getId()));
        verify(itemRequestRepository, never()).findAllByRequesterId(anyLong());
    }

    @Test
    void getRequest() {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setItems(List.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        service.getRequest(1L, 1L);
        verify(itemRequestRepository).findById(anyLong());
    }

    @Test
    void getRequestUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRequest(1L, 1L));
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRequest(1L, 1L));
    }
}
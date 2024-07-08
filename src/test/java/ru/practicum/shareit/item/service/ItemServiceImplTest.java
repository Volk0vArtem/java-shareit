package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapperImpl;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentMapperImpl;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();

    private final EasyRandom generator = new EasyRandom();

    User user;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemMapper, new BookingMapperImpl(), commentMapper);
        user = generator.nextObject(User.class);
        itemDto = generator.nextObject(ItemDto.class);
    }

    @Test
    void saveItem() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.getReferenceById(user.getId()))
                .thenReturn(user);

        itemService.saveItem(itemDto, user.getId());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveItemUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(itemDto, user.getId()));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void getItemsByID() {

    }

    @Test
    void patchItem() {
        ItemDto patchedItem = generator.nextObject(ItemDto.class);
        patchedItem.setId(itemDto.getId());

        Item item = itemMapper.toItem(patchedItem);
        item.setOwner(user);

        when(itemRepository.getReferenceById(itemDto.getId()))
                .thenReturn(item);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto patchedDto = itemService.patchItem(itemMapper.toItemDto(item), item.getId(), user.getId());

        assertEquals(patchedDto.getName(), item.getName());
        assertEquals(patchedDto.getAvailable(), item.getAvailable());
        assertEquals(patchedDto.getDescription(), item.getDescription());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void patchItemNoUserId() {
        ItemDto patchedItem = generator.nextObject(ItemDto.class);
        patchedItem.setId(itemDto.getId());

        Item item = itemMapper.toItem(patchedItem);
        item.setOwner(user);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.patchItem(itemMapper.toItemDto(item), item.getId(), null));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void patchItemUserForbidden() {
        ItemDto patchedItem = generator.nextObject(ItemDto.class);
        patchedItem.setId(itemDto.getId());

        Item item = itemMapper.toItem(patchedItem);
        item.setOwner(user);

        when(itemRepository.getReferenceById(itemDto.getId()))
                .thenReturn(item);

        assertThrows(ForbiddenException.class,
                () -> itemService.patchItem(itemMapper.toItemDto(item), item.getId(), user.getId() + 1));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void searchEmptyText() {
        assertEquals(Collections.emptyList(), itemService.search("", PageRequest.of(0, 10)));
        verify(itemRepository, never()).search(anyString(), any(PageRequest.class));
    }

    @Test
    void search() {
        Item item = generator.nextObject(Item.class);
        when(itemRepository.search("text", PageRequest.of(0, 10)))
                .thenReturn(List.of(item));
        itemService.search("text", PageRequest.of(0, 10));
        verify(itemRepository).search("text", PageRequest.of(0, 10));
    }

    @Test
    void postComment() {
        CommentDto comment = generator.nextObject(CommentDto.class);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(itemMapper.toItem(itemDto)));
        when(bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(2L);

        itemService.postComment(comment, itemDto.getId(), user.getId());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void postCommentUserNotFound() {
        CommentDto comment = generator.nextObject(CommentDto.class);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.postComment(comment, itemDto.getId(), user.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void postCommentItemNotFound() {
        CommentDto comment = generator.nextObject(CommentDto.class);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.postComment(comment, itemDto.getId(), user.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void postCommentItemWasNotInShare() {
        CommentDto comment = generator.nextObject(CommentDto.class);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(itemMapper.toItem(itemDto)));
        when(bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(0L);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.postComment(comment, itemDto.getId(), user.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
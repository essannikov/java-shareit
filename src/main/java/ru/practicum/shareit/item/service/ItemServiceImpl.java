package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoDates;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoDates> getAllItemsByUser(Long userId) {
        User user = getUser(userId);

        List<Item> items = itemRepository.findAllByOwnerId(user.getId(),
                Sort.by(Sort.Direction.ASC, "id"));
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Booking> lastBookings = bookingRepository
                .findLastBookingsByOwner(user.getId(), BookingStatus.APPROVED, now)
                .stream().collect(Collectors.toMap(b -> b.getItem().getId(), b -> b,
                        (existing, replacement) -> existing));
        Map<Long, Booking> nextBookings = bookingRepository
                .findNextBookingsByOwner(user.getId(), BookingStatus.APPROVED, now)
                .stream().collect(Collectors.toMap(b -> b.getItem().getId(), b -> b,
                        (existing, replacement) -> existing));

        List<Comment> comments = commentRepository.findByItemOwnerId(user.getId());
        Map<Long, List<CommentDtoOut>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDtoOut, Collectors.toList())));

        return items.stream()
                .map(item -> ItemMapper.toItemDtoDates(item,
                        Optional.ofNullable(lastBookings.get(item.getId()))
                                .map(Booking::getStart).orElse(null),
                        Optional.ofNullable(nextBookings.get(item.getId()))
                                .map(Booking::getStart).orElse(null),
                        commentsByItemId.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoDates getItemById(Long itemId) {
        Item item = getItem(itemId);

        List<CommentDtoOut> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(Collectors.toList());;

        return ItemMapper.toItemDtoDates(item, null, null, comments);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userIdChange) {
        checkItemDto(itemDto);
        User userChange = getUser(userIdChange);
        Item item = ItemMapper.toItem(itemDto, userChange, null);
        return Optional.of(itemRepository.save(item))
                .map(i -> ItemMapper.toItemDto(i, null)).orElse(null);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userIdChange) {
        boolean changeFlag = false;
        checkItemDto(itemDto);
        User userChange = getUser(userIdChange);
        Item item = getItem(itemDto.getId());

        if (!userChange.getId().equals(item.getOwner().getId())) {
            throw new ValidationException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
            changeFlag = true;
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
            changeFlag = true;
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
            changeFlag = true;
        }

        if (!changeFlag) {
            throw new ValidationException("Нет данных для изменения");
        }

        return Optional.of(itemRepository.save(item))
                .map(i -> ItemMapper.toItemDto(i, null)).orElse(null);
    }

    @Transactional
    @Override
    public boolean deleteItemById(Long itemId) {
        itemRepository.deleteById(getItem(itemId).getId());
        return true;
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(i -> ItemMapper.toItemDto(i, null)).toList();
    }

    @Transactional
    @Override
    public CommentDtoOut addComment(Long itemId, Long userId, CommentDtoIn createCommentDto) {
        Item item = getItem(itemId);
        User user = getUser(userId);

        Booking booking = bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                        BookingStatus.APPROVED, LocalDateTime.now())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("Бронирование не найдено"));

        Comment comment = CommentMapper.toComment(createCommentDto, item, user, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDtoOut(savedComment);
    }

    protected void checkItemDto(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Ошибка в данных");
        }
    }

    protected User getUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Не задан id пользователя");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return user.get();
    }

    protected Item getItem(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Не задан id вещи");
        }

        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", itemId));
        }

        return item.get();
    }
}

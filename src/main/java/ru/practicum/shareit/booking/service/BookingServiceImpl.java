package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingDtoOut> getAllBookingsByUserAndState(Long userId, String state) {
        List<Booking> bookings;

        User user = getUser(userId);
        BookingState bookingState = getBookingState(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBookerId(user.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        user.getId(), LocalDateTime.now(), LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(user.getId(), LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndEndIsAfter(user.getId(), LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = new ArrayList<>();
        }

        return bookings.stream().map(BookingMapper::toBookingDtoOut).toList();
    }

    @Override
    public List<BookingDtoOut> getAllBookingsByOwnerAndState(Long ownerId, String state) {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        User user = getUser(ownerId);
        BookingState bookingState = getBookingState(state);

        List<Long> itemIds = itemRepository.findAllByOwnerId(user.getId(), Sort.by(Sort.Direction.ASC, "id"))
                .stream().map(Item::getId).toList();
        if (itemIds.isEmpty()) {
            return Collections.emptyList();
        }

        switch (bookingState) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItemIdIn(itemIds,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(itemIds, now,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemIdInAndEndBefore(itemIds, now,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemIdInAndStartAfter(itemIds, now,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemIdInAndStatus(itemIds, BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemIdInAndStatus(itemIds, BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
        }

        return bookings.stream().map(BookingMapper::toBookingDtoOut).toList();
    }

    @Override
    public BookingDtoOut getBookingById(Long userId, Long bookingId) {
        User user = getUser(userId);
        Booking booking = getBooking(bookingId);

        if (booking.getBooker().getId().equals(user.getId()) ||
                booking.getItem().getOwner().getId().equals(user.getId())) {
            return BookingMapper.toBookingDtoOut(booking);
        } else {
            throw new ValidationException("Получение данных о конкретном бронировании, " +
                    "может быть выполнено либо автором бронирования, либо владельцем вещи");
        }
    }

    @Transactional
    @Override
    public BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId) {
        User user = getUser(userId);
        Item item = getItem(bookingDtoIn.getItemId());

        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart()) ||
                bookingDtoIn.getEnd().isEqual(bookingDtoIn.getStart())) {
            throw new ValidationException("Дата и время начала бронирования должны быть раньше " +
                    "даты и времени конца бронирования");
        }
        if (!item.getAvailable()) {
            throw new ValidationException(
                    String.format("Вещь с id = %d недоступна для бронирования", item.getId()));
        }
        if (item.getOwner().getId().equals(user.getId())) {
            throw new ValidationException(
                    String.format("Пользователь с id = %d, владелец вещи", user.getId()));
        }

        Booking booking = BookingMapper.toBooking(null, bookingDtoIn, item, user, BookingStatus.WAITING);
        return Optional.of(bookingRepository.save(booking))
                .map(BookingMapper::toBookingDtoOut).orElse(null);
    }

    @Transactional
    @Override
    public BookingDtoOut updateBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException(
                    String.format("Пользователь с id = %d, не является владельцем вещи с id: %d",
                            userId, item.getId()));
        }
        User user = getUser(userId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException(
                    String.format("Ошибка статуса бронирования с id = %d", booking.getId()));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return Optional.of(bookingRepository.save(booking))
                .map(BookingMapper::toBookingDtoOut).orElse(null);
    }

    protected User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    protected BookingState getBookingState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("Статус = %s не найден", state));
        }
    }

    protected Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId)));
    }

    protected Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id = %d не найдено", itemId)));
    }
}
package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.check.OnCreate;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingDtoOut>> getAllBookingsByUserAndState(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен список всех бронирований текущего пользователя с id = {}, state = {}.", userId, state);
        return ResponseEntity.ok(bookingService.getAllBookingsByUserAndState(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOut>> getAllBookingsByOwnerAndState(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен список всех бронирований вещей владельца с id = {}, state = {}." , userId, state);
        return ResponseEntity.ok(bookingService.getAllBookingsByOwnerAndState(userId, state));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> getBookingById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        log.info("Получено бронирование с id = {}.", bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @PostMapping
    public ResponseEntity<BookingDtoOut> createBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Validated(OnCreate.class) @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Добавлен новый запрос на бронирование: {}", bookingDtoIn);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookingDtoIn, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> updateItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Обновлено бронирование id: {}.", bookingId);
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, approved, userId));
    }
}
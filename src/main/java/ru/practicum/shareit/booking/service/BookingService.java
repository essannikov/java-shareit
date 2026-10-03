package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    List<BookingDtoOut> getAllBookingsByUserAndState(Long userId, String state);

    List<BookingDtoOut> getAllBookingsByOwnerAndState(Long userId, String state);

    BookingDtoOut getBookingById(Long userId, Long bookingId);

    BookingDtoOut createBooking(BookingDtoIn bookingDto, Long userId);

    BookingDtoOut updateBooking(Long bookingId, Boolean approved, Long userId);
}
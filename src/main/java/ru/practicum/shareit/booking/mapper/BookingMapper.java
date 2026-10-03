package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return new BookingDtoOut(booking.getId(), booking.getStart(), booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem(), null),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static Booking toBooking(Long id, BookingDtoIn bookingDto, Item item, User booker, BookingStatus status) {
        return new Booking(id, bookingDto.getStart(), bookingDto.getEnd(),
                item, booker, status);
    }
}

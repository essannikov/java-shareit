package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.OnCreate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoIn {
    @NotNull(groups = {OnCreate.class},
            message = "Не заданы дата и время начала бронирования.")
    @FutureOrPresent(groups = {OnCreate.class},
            message = "Дата и время начала бронирования не могут быть в прошлом.")
    private LocalDateTime start;

    @NotNull(groups = {OnCreate.class},
            message = "Не заданы дата и время конца бронирования.")
    @Future(groups = {OnCreate.class},
            message = "Дата и время конца бронирования не могут быть в прошлом.")
    private LocalDateTime end;

    @NotNull(groups = {OnCreate.class},
            message = "Не задана вещь.")
    private Long itemId;
}

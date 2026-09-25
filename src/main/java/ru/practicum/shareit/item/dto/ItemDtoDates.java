package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.ContentNotBlank;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoDates {
    private Long id;

    @NotBlank(groups = {OnCreate.class})
    @ContentNotBlank(groups = {OnUpdate.class})
    private String name;

    @NotBlank(groups = {OnCreate.class})
    @ContentNotBlank(groups = {OnUpdate.class})
    private String description;

    @NotNull(groups = {OnCreate.class})
    private Boolean available;

    private Long request;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDtoOut> comments;
}

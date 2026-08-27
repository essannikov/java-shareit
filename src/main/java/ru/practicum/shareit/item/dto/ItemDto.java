package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.check.ContentNotBlank;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;

@Data
public class ItemDto {
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

    public ItemDto(Long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}

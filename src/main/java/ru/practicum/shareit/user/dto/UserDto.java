package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = {OnCreate.class})
    private String name;

    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(groups = {OnCreate.class})
    private String email;
}

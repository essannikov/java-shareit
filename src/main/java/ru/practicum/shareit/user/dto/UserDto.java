package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.check.OnCreate;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = {OnCreate.class})
    private String name;
    @Email(groups = {OnCreate.class})
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

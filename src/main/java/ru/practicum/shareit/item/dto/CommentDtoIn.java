package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.OnCreate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoIn {
    @NotBlank(groups = {OnCreate.class},
            message =  "Комментарий не может быть пустым.")
    private String text;
}

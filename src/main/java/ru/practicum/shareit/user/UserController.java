package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = userService.getAllUsers();
        log.info("Получен список пользователей, количество = {}", usersDto.size());
        return usersDto;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", userDto.getId());
        return userDto;
    }

    @PostMapping
    public UserDto saveUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        UserDto userDtoNew = userService.saveUser(userDto);
        log.info("Добавлен новый пользователь: {}", userDtoNew);
        return userDtoNew;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto userDtoNew = userService.updateUser(userDto);
        log.info("Обновлен пользователь: {}", userDtoNew);
        return userDtoNew;
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUserById(@PathVariable Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        return userService.deleteUserById(userId);
    }
}

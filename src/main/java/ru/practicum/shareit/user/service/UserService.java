package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    boolean deleteUserById(Long userId);
}

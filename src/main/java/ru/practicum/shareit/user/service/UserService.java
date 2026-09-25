package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    @Transactional
    UserDto saveUser(UserDto userDto);

    @Transactional
    UserDto updateUser(UserDto userDto);

    @Transactional
    boolean deleteUserById(Long userId);
}

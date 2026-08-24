package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    Optional<User> saveUser(User user);

    Optional<User> updateUser(User user);

    boolean deleteUserById(Long userId);
}
package ru.practicum.shareit.user.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long userIdCount = 0;

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> saveUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteUserById(Long userId) {
        return users.remove(userId) != null;
    }

    private static Long getNextId() {
        return ++userIdCount;
    }
}

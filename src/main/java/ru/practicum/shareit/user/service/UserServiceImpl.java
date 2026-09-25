package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(getUser(userId).getId())
                .map(UserMapper::toUserDto).orElse(null);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        checkUserDto(userDto, false);
        return Optional.of(userRepository.save(UserMapper.toUser(userDto)))
                .map(UserMapper::toUserDto).orElse(null);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        boolean changeFlag = false;
        checkUserDto(userDto, true);
        User user = getUser(userDto.getId());

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
            changeFlag = true;
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
            changeFlag = true;
        }

        if (!changeFlag) {
            throw new ValidationException("Нет данных для изменения");
        }

        return Optional.of(userRepository.save(user))
                .map(UserMapper::toUserDto).orElse(null);
    }

    @Transactional
    @Override
    public boolean deleteUserById(Long userId) {
        userRepository.deleteById(getUser(userId).getId());
        return true;
    }

    protected void checkUserDto(UserDto userDto, boolean updateFlag) {
        Long userId;
        boolean checkEmailFlag = false;

        if (userDto == null) {
            throw new ValidationException("Ошибка в данных");
        }

        if (updateFlag) {
            userId = getUser(userDto.getId()).getId();
            if (userDto.getEmail() != null) {
                checkEmailFlag = true;
            }
        } else {
            userId = null;
            checkEmailFlag = true;
        }

        if (checkEmailFlag && getEmailCountExclUserId(userDto.getEmail(), userId) > 0) {
            throw new ConflictException(
                    String.format("Пользователь с email = %s, уже существует", userDto.getEmail()));
        }
    }

    protected User getUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Не задан id пользователя");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return user.get();
    }

    protected long getEmailCountExclUserId(String email, Long userId) {
        return userRepository.findAllByEmailIgnoreCase(email).stream()
                .filter(u -> !u.getId().equals(userId))
                .count();
    }
}

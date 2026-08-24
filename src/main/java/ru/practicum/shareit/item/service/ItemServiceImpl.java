package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        return itemRepository.getAllItemsByUser(getUser(userId).getId())
                .stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(getItem(itemId));
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userIdChange) {
        checkItemDto(itemDto);
        User userChange = getUser(userIdChange);
        Item item = ItemMapper.toItem(itemDto, userChange, null);
        return itemRepository.saveItem(item).map(ItemMapper::toItemDto).orElse(null);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userIdChange) {
        boolean changeFlag = false;
        checkItemDto(itemDto);
        User userChange = getUser(userIdChange);
        Item item = getItem(itemDto.getId());

        if (!userChange.getId().equals(item.getOwner().getId())) {
            throw new ValidationException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
            changeFlag = true;
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
            changeFlag = true;
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
            changeFlag = true;
        }

        if (!changeFlag) {
            throw new ValidationException("Нет данных для изменения");
        }

        return itemRepository.updateItem(item).map(ItemMapper::toItemDto).orElse(null);
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        return itemRepository.deleteItemById(getItem(itemId).getId());
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findItems(text).stream().map(ItemMapper::toItemDto).toList();
    }

    protected void checkItemDto(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Ошибка в данных");
        }
    }

    protected User getUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Не задан id пользователя");
        }

        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return user.get();
    }

    protected Item getItem(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Не задан id вещи");
        }

        Optional<Item> item = itemRepository.getItemById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", itemId));
        }

        return item.get();
    }
}

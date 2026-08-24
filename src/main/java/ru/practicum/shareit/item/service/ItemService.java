package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsByUser(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto saveItem(ItemDto itemDto, Long userIdChange);

    ItemDto updateItem(ItemDto itemDto, Long userIdChange);

    boolean deleteItemById(Long itemId);

    List<ItemDto> findItems(String text);
}
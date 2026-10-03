package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoDates;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDtoDates> getAllItemsByUser(Long userId);

    ItemDtoDates getItemById(Long itemId);

    @Transactional
    ItemDto saveItem(ItemDto itemDto, Long userIdChange);

    @Transactional
    ItemDto updateItem(ItemDto itemDto, Long userIdChange);

    @Transactional
    boolean deleteItemById(Long itemId);

    List<ItemDto> findItems(String text);

    CommentDtoOut addComment(Long itemId, Long userId, CommentDtoIn commentDto);
}
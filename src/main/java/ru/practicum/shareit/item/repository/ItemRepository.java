package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllItemsByUser(Long userId);

    Optional<Item> getItemById(Long itemId);

    Optional<Item> saveItem(Item item);

    Optional<Item> updateItem(Item item);

    boolean deleteItemById(Long itemId);

    List<Item> findItems(String text);
}

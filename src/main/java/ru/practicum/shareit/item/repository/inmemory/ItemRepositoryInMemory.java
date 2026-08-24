package ru.practicum.shareit.item.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static long itemIdCount = 0;

    @Override
    public List<Item> getAllItemsByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId)).toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Optional<Item> saveItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            items.replace(item.getId(), item);
            return Optional.of(item);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        return items.remove(itemId) != null;
    }

    @Override
    public List<Item> findItems(String text) {
        String textSearch = text.toLowerCase();

        return items.values().stream().filter(item ->
                ( item.getName().toLowerCase().contains(textSearch) ||
                        item.getDescription().toLowerCase().contains(textSearch) ) &&
                        item.getAvailable().equals(true)).toList();
    }

    private static Long getNextId() {
        return ++itemIdCount;
    }
}

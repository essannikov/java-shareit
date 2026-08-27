package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(xSharerUserId) Long userId) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId);
        log.info("Получен список вещей пользователя с id = {}, количество = {}.", userId, items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        log.info("Получена вещь с id = {}.", itemId);
        return item;
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader(xSharerUserId) Long userIdChange,
                            @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        ItemDto itemDtoNew = itemService.saveItem(itemDto, userIdChange);
        log.info("Добавлена новая вещь: {}.", itemDtoNew);
        return itemDtoNew;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(xSharerUserId) Long userIdChange,
                              @PathVariable Long itemId,
                              @Validated(OnUpdate.class) @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        ItemDto itemDtoNew = itemService.updateItem(itemDto, userIdChange);
        log.info("Обновлена вещь: {}.", itemDtoNew);
        return itemDtoNew;
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text) {
        List<ItemDto> items = itemService.findItems(text);
        log.info("Получен список вещей с текстом: \"{}\", количество = {}.",
                text, items.size());
        return items;
    }
}

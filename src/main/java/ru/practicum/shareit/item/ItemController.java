package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.check.OnCreate;
import ru.practicum.shareit.check.OnUpdate;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoDates;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoDates> getAllItemsByUser(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        List<ItemDtoDates> items = itemService.getAllItemsByUser(userId);
        log.info("Получен список вещей пользователя с id = {}, количество = {}.", userId, items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDtoDates getItemById(@PathVariable Long itemId) {
        ItemDtoDates item = itemService.getItemById(itemId);
        log.info("Получена вещь с id = {}.", itemId);
        return item;
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader(X_SHARER_USER_ID) Long userIdChange,
                            @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        ItemDto itemDtoNew = itemService.saveItem(itemDto, userIdChange);
        log.info("Добавлена новая вещь: {}.", itemDtoNew);
        return itemDtoNew;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) Long userIdChange,
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

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @PathVariable Long itemId,
                                    @Validated(OnCreate.class) @RequestBody CommentDtoIn commentDto) {
        log.info("Комментарий для вещи : {}.", itemId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}

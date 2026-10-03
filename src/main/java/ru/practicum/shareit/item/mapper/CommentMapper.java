package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDtoOut toCommentDtoOut(Comment comment) {
        return new CommentDtoOut(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment toComment(CommentDtoIn commentDtoIn, Item item, User user, LocalDateTime created) {
        return new Comment(null, commentDtoIn.getText(), item, user, created);
    }
}

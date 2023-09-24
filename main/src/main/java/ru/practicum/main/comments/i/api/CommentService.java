package ru.practicum.main.comments.i.api;

import ru.practicum.main.comments.model.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Long userId, Comment comment);

    Comment updateComment(Long userId, Long commentId, Comment comment);

    void deleteComment(Long userId, Long commentId);

    Comment getCommentById(Long commentId);

    List<Comment> getCommentsAboutEvent(Long commentId, int from, int size);
}
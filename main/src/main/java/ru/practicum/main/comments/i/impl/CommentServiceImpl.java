package ru.practicum.main.comments.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comments.i.api.CommentRepository;
import ru.practicum.main.comments.i.api.CommentService;
import ru.practicum.main.comments.model.Comment;
import ru.practicum.main.event.i.api.EventRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.exception.ForbiddenOperationException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public Comment saveComment(Long userId, Comment comment) {
        log.info("Service layer: POST /user/{userId}/comments request from user with id: {} obtained.", userId);

        Event event = comment.getEvent();

        if (event.getState() != State.PUBLISHED) {
            throw new ForbiddenOperationException("Its impossible to post comment about unpublished event.");
        }

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public Comment updateComment(Long userId, Long commentId, Comment comment) {
        log.info("Service layer: PATCH /user/{userId}/comments/{commentId} request for user with id: {} and " +
                "comment with id: {} obtained.", userId, commentId);

        Comment commentFound = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityDoesNotExistException("Comment with id: " + commentId + " doesn't exist in database."));

        if (!commentFound.getAuthor().getId().equals(userId)) {
            throw new ForbiddenOperationException("Impossible to edit not your own comment");
        }

        commentFound.setUpdated(LocalDateTime.now());
        commentFound.setText(comment.getText());

        return commentRepository.save(commentFound);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        log.info("Service layer: DELETE /user/{userId}/comments{commentId}/delete request for user with id: {} " +
                "and comment with id: {} obtained.", userId, commentId);

        Comment commentFound = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityDoesNotExistException("Comment with id: " + commentId + " doesn't exist in database."));

        if (!commentFound.getAuthor().getId().equals(userId)) {
            throw new ForbiddenOperationException("Impossible to delete not your own comment.");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        log.info("Service layer: GET /comments/{commentId} request for comment with id: {} obtained.", commentId);

        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityDoesNotExistException("Comment with id: " + commentId + " doesn't exist in database."));
    }

    @Override
    public List<Comment> getCommentsAboutEvent(Long eventId, int from, int size) {
        log.info("Controller layer: GET/comments/events/{eventId} request for event with id: {} obtained.", eventId);

        eventRepository.findById(eventId).orElseThrow(() ->
                new EntityDoesNotExistException("Event with id: " + eventId + " doesn't exist in database."));

        return commentRepository.findCommentsByEventId(eventId, PageRequest.of(from / size, size));
    }
}
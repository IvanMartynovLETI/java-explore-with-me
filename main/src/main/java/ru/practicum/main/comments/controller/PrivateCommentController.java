package ru.practicum.main.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comments.dto.CommentDtoMapper;
import ru.practicum.main.comments.dto.CommentFullDto;
import ru.practicum.main.comments.dto.NewCommentDto;
import ru.practicum.main.comments.dto.UpdateCommentUserRequest;
import ru.practicum.main.comments.i.api.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;
    private final CommentDtoMapper commentDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto saveComment(@PathVariable Long userId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Controller layer: POST /user/{userId}/comments request from user with id: {} obtained.", userId);

        return commentDtoMapper.commentToCommentFullDto(commentService.saveComment(userId,
                commentDtoMapper.newCommentDtoToComment(userId, newCommentDto)));
    }

    @PatchMapping("/{commentId}")
    public CommentFullDto updateComment(@PathVariable Long userId,
                                        @PathVariable Long commentId,
                                        @Valid @RequestBody UpdateCommentUserRequest updateCommentUserRequest) {
        log.info("Controller layer: PATCH /user/{userId}/comments/{commentId} request for user with id: {} and " +
                "comment with id: {} obtained.", userId, commentId);

        return commentDtoMapper.commentToCommentFullDto(commentService.updateComment(userId, commentId,
                commentDtoMapper.commentUpdateUserRequestToComment(userId, commentId, updateCommentUserRequest)));
    }

    @DeleteMapping("/{commentId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Controller layer: DELETE /user/{userId}/comments{commentId}/delete request for user with id: {} " +
                "and comment with id: {} obtained.", userId, commentId);

        commentService.deleteComment(userId, commentId);
    }
}
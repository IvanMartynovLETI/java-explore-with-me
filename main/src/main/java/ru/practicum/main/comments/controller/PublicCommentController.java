package ru.practicum.main.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comments.dto.CommentDtoMapper;
import ru.practicum.main.comments.dto.CommentFullDto;
import ru.practicum.main.comments.dto.CommentShortDto;
import ru.practicum.main.comments.i.api.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@Validated
public class PublicCommentController {
    private final CommentService commentService;
    private final CommentDtoMapper commentDtoMapper;

    @GetMapping("/{commentId}")
    public CommentFullDto getCommentById(@PathVariable Long commentId) {
        log.info("Controller layer: GET /comments/{commentId} request for comment with id: {} obtained.", commentId);

        return commentDtoMapper.commentToCommentFullDto(commentService.getCommentById(commentId));
    }

    @GetMapping("/events/{eventId}")
    public List<CommentShortDto> getCommentsAboutEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Controller layer: GET/comments/events/{eventId} request for event with id: {} obtained.", eventId);

        return commentDtoMapper.commentsToCommentShortDtos(commentService.getCommentsAboutEvent(eventId, from, size));
    }
}
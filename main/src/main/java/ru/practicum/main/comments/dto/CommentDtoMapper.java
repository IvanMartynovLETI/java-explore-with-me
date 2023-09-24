package ru.practicum.main.comments.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.comments.model.Comment;
import ru.practicum.main.event.dto.EventDtoMapper;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.user.dto.UserDtoMapper;
import ru.practicum.main.user.i.api.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentDtoMapper {
    private final UserService userService;
    private final EventService eventService;
    private final UserDtoMapper userDtoMapper;
    private final EventDtoMapper eventDtoMapper;

    public Comment newCommentDtoToComment(Long userId, NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setAuthor(userService.getUserById(userId));
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());
        comment.setEvent(eventService.findEventById(newCommentDto.getEvent()));

        return comment;
    }

    public CommentFullDto commentToCommentFullDto(Comment comment) {
        CommentFullDto commentFullDto = new CommentFullDto();
        commentFullDto.setId(comment.getId());
        commentFullDto.setAuthor(userDtoMapper.userToUserShortDto(comment.getAuthor()));
        commentFullDto.setEvent(eventDtoMapper.eventToEventShortDto(comment.getEvent()));
        commentFullDto.setText(comment.getText());
        commentFullDto.setCreated(comment.getCreated());
        LocalDateTime updated = comment.getUpdated();
        if (updated != null) {
            commentFullDto.setUpdated(updated);
        }

        return commentFullDto;
    }

    public Comment commentUpdateUserRequestToComment(Long userId, Long commentId,
                                                     UpdateCommentUserRequest updateCommentUserRequest) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(userService.getUserById(userId));
        comment.setText(updateCommentUserRequest.getText());
        comment.setUpdated(LocalDateTime.now());

        return comment;
    }

    public CommentShortDto commentToCommentShortDto(Comment comment) {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setId(comment.getId());
        commentShortDto.setAuthor(userDtoMapper.userToUserShortDto(comment.getAuthor()));
        commentShortDto.setText(comment.getText());
        commentShortDto.setCreated(comment.getCreated());
        LocalDateTime updated = comment.getUpdated();
        if (updated != null) {
            commentShortDto.setUpdated(updated);
        }

        return commentShortDto;
    }

    public List<CommentShortDto> commentsToCommentShortDtos(List<Comment> comments) {
        if (!comments.isEmpty()) {
            return comments
                    .stream()
                    .map(this::commentToCommentShortDto)
                    .sorted(Comparator.comparing(CommentShortDto::getCreated).reversed())
                    .sorted(Comparator.comparing(CommentShortDto::getUpdated).reversed())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
package ru.practicum.main.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {
    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    @EqualsAndHashCode.Exclude
    private Long id;
    private UserShortDto author;
    private EventShortDto event;
    private String text;
    @JsonFormat(pattern = DATE_TIME_FORMATTER, shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;
    @JsonFormat(pattern = DATE_TIME_FORMATTER, shape = JsonFormat.Shape.STRING)
    private LocalDateTime updated;
}
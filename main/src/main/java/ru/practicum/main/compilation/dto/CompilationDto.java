package ru.practicum.main.compilation.dto;

import lombok.*;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
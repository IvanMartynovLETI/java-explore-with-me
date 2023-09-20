package ru.practicum.main.compilation.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.dto.EventDtoMapper;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationDtoMapper {
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    public Compilation newCompilationDToCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null) {
            events = newCompilationDto.getEvents()
                    .stream()
                    .map(eventService::getEventById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        compilation.setEvents(events);

        return compilation;
    }

    public CompilationDto compilationToCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(eventDtoMapper.eventsToEventShortDtos(compilation.getEvents()));

        return compilationDto;
    }

    public Compilation updateCompilationRequestToCompilation(Long compId,
                                                             UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = new Compilation();
        compilation.setId(compId);
        compilation.setPinned(updateCompilationRequest.getPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());
        List<Event> events = new ArrayList<>();

        if (updateCompilationRequest.getEvents() != null) {
            events = updateCompilationRequest.getEvents()
                    .stream()
                    .map(eventService::getEventById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        compilation.setEvents(events);

        return compilation;
    }

    public List<CompilationDto> compilationsToCompilationDtos(List<Compilation> compilations) {

        return compilations
                .stream()
                .map(this::compilationToCompilationDto)
                .collect(Collectors.toList());
    }
}
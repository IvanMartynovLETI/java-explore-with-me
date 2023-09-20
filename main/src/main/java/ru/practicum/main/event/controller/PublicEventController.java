package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDtoMapper;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    @GetMapping
    public List<EventShortDto> getAllPublicEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = "sort", required = false, defaultValue = "EVENT_DATE") String sort,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size,
            HttpServletRequest httpServletRequest) {
        log.info("Controller layer: GET /events request obtained.");

        List<Event> events = eventService.getAllPublicEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, from, size, httpServletRequest);

        return eventDtoMapper.getSortedShortDtos(events, eventService.getStatisticsOfViews(events), sort);
    }

    @GetMapping("/{id}")
    public EventFullDto getFullInfoAboutEvent(@PathVariable Long id,
                                              HttpServletRequest httpServletRequest) {
        log.info("Controller layer: GET /events/{id} request for event with id: {} obtained.", id);

        Event event = eventService.getFullInfoAboutPublishedEvent(id, httpServletRequest);
        List<Event> events = List.of(event);

        return eventDtoMapper.eventToEventFullDtoWithViews(event, eventService.getStatisticsOfViews(events));
    }
}
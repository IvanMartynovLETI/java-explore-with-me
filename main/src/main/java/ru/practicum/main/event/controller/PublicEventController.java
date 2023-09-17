package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDtoMapper;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.i.api.EventService;

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

        return eventDtoMapper.getSortedShortDtos(eventService.getAllPublicEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, from, size, httpServletRequest), eventService
                .getStatisticsOfViews(eventService.getAllPublicEvents(text, categories, paid, rangeStart, rangeEnd,
                        onlyAvailable, from, size, httpServletRequest)), sort);
    }

    @GetMapping("/{id}")
    public EventFullDto getFullInfoAboutEvent(@PathVariable Long id,
                                              HttpServletRequest httpServletRequest) {
        log.info("Controller layer: GET /events/{id} request for event with id: {} obtained.", id);

        return eventDtoMapper.eventToEventFullDtoWithViews(eventService.getFullInfoAboutPublishedEvent(id,
                httpServletRequest), eventService.getStatisticsOfViews(List.of(eventService
                .getFullInfoAboutPublishedEvent(id, httpServletRequest))));
    }
}
package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDtoMapper;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.event.model.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class AdminEventController {
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    @GetMapping
    List<EventFullDto> getAllEventsByAdmin(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<State> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Controller layer: GET /admin/events request obtained.");

        return eventDtoMapper.eventsToEventFullDtos(eventService.getAllEventsByAdmin(users, states, categories,
                rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Controller layer: PATCH /admin/events/{eventId} request for event with id : {} obtained.", eventId);

        return eventDtoMapper.eventToEventFullDto(eventService.updateEventByAdmin(eventId, updateEventAdminRequest));
    }
}
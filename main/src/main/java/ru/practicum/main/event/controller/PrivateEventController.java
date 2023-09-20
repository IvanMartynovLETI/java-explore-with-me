package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.dto.RequestDtoMapper;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;
    private final RequestDtoMapper requestDtoMapper;

    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Controller layer: GET /users/{userId}/events request for user with id: {} obtained.", userId);

        return eventDtoMapper.eventsToEventShortDtos(eventService.getAllEventsByUserId(userId, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEventByUserId(@PathVariable Long userId,
                                          @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Controller layer: POST /users/{userId}/events request for user with id: {} obtained.", userId);

        return eventDtoMapper.eventToEventFullDto(eventService.createNewEventByUser(userId,
                eventDtoMapper.newEventDtoToEvent(newEventDto, userId)));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Controller layer: GET /users/{userId}/events/{eventId} request for user with id: {} and event " +
                "with id: {} obtained.", eventId, userId);

        return eventDtoMapper.eventToEventFullDto(eventService.getEventByUserIdAndEventId(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Controller layer: PATCH /users/{userId}/events/{eventId} request for user with id: {} and event " +
                "with id: {} obtained.", eventId, userId);

        return eventDtoMapper.eventToEventFullDto(eventService.updateEventByUser(userId, eventId,
                updateEventUserRequest));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsToEventsByEventAndUser(@PathVariable Long userId,
                                                                              @PathVariable Long eventId) {
        log.info("Controller layer: GET /users/{userId}/events/{eventId}/requests request for user with id: {} and" +
                " event with id: {} obtained.", eventId, userId);

        return requestDtoMapper.requestsToDtos(eventService.getAllRequestsToEventsByEventAndUser(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusOfRequests(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequest
                                                                         eventRequestStatusUpdateRequest) {
        log.info("Controller layer: PATCH /users/{userId}/events/{eventId}/requests request for user with id: {} and " +
                "event with id: {} obtained.", userId, eventId);

        return eventService.updateStatusOfRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
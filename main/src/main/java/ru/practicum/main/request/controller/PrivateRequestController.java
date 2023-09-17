package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.dto.RequestDtoMapper;
import ru.practicum.main.request.i.api.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;
    private final RequestDtoMapper requestDtoMapper;

    @GetMapping
    public List<ParticipationRequestDto> getAllRequestsByUserId(@PathVariable Long userId) {
        log.info("Controller layer: GET /users/{userId}/requests request for user with id: {} obtained.", userId);

        return requestDtoMapper.requestsToDtos(requestService.getAllRequestsByUserId(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PathVariable Long userId,
                                               @RequestParam Long eventId) {
        log.info("Controller layer: POST /users/{userId}/requests request for user with id: {} and request with " +
                "id: {} obtained.", userId, eventId);

        return requestDtoMapper.requestToParticipationRequestDto(requestService.saveRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Controller layer: PATCH /users/{userId}/requests/{requestId}/cancel request for cancel request " +
                "with id: {} of user with id: {} obtained.", requestId, userId);

        return requestDtoMapper.requestToParticipationRequestDto(requestService.updateRequest(userId, requestId));
    }
}
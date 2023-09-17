package ru.practicum.main.request.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.i.api.EventRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.exception.IncorrectOperationException;
import ru.practicum.main.request.dto.Status;
import ru.practicum.main.request.i.api.RequestRepository;
import ru.practicum.main.request.i.api.RequestService;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.user.i.api.UserRepository;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<Request> getAllRequestsByUserId(Long userId) {
        log.info("Service layer: GET /users/{userId}/requests request for user with id: {} obtained.", userId);

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            String userWarning = "User with id: " + userId + " doesn't exist in database";
            throw new EntityDoesNotExistException(userWarning);
        }

        return requestRepository.findRequestsByRequesterId(userId);
    }

    @Transactional
    @Override
    public Request saveRequest(Long userId, Long eventId) {
        log.info("Service layer: POST /users/{userId}/requests request for user with id: {} and request with " +
                "id: {} obtained.", userId, eventId);

        String warning;
        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            warning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            warning = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            warning = "It's impossible to send request more than once";
            throw new IncorrectOperationException(warning);
        }

        if (eventFound.getInitiator().getId().equals(userId)) {
            warning = "It's impossible to send request to your own event.";
            throw new IncorrectOperationException(warning);
        }

        if (!eventFound.getState().equals(State.PUBLISHED)) {
            warning = "It's impossible to send request to unconfirmed event.";
            throw new IncorrectOperationException(warning);
        }

        if (requestRepository.findAllByEventAndStatus(eventFound, Status.CONFIRMED).size() ==
                eventFound.getParticipantLimit() && eventFound.getParticipantLimit() != 0) {
            warning = "There is not free places to take part in event.";
            throw new IncorrectOperationException(warning);
        }

        Request request = new Request();
        request.setRequester(userFound);
        request.setEvent(eventFound);

        if (eventFound.getParticipantLimit() == 0 || !eventFound.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }

        request.setCreated(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Transactional
    @Override
    public Request updateRequest(Long userId, Long requestId) {
        log.info("Service layer: PATCH /users/{userId}/requests/{requestId}/cancel request for cancel request " +
                "with id: {} of user with id: {} obtained.", requestId, userId);

        String warning;
        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            warning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        Request requestFound = requestRepository.findRequestById(requestId);

        if (requestFound == null) {
            warning = "Request with id: " + requestId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        if (!requestFound.getRequester().getId().equals(userId)) {
            warning = "No such request.";
            throw new EntityDoesNotExistException(warning);
        }

        requestFound.setStatus(Status.CANCELED);

        return requestRepository.save(requestFound);
    }
}
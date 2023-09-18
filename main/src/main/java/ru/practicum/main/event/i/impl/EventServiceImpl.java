package ru.practicum.main.event.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticsDto;
import ru.practicum.main.category.i.api.CategoryRepository;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.StateAction;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.i.api.EventRepository;
import ru.practicum.main.event.i.api.EventService;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.exception.ForbiddenOperationException;
import ru.practicum.main.exception.IncorrectDataException;
import ru.practicum.main.exception.IncorrectOperationException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.RequestDtoMapper;
import ru.practicum.main.request.dto.Status;
import ru.practicum.main.request.i.api.RequestRepository;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.user.i.api.UserRepository;
import ru.practicum.main.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestDtoMapper requestDtoMapper;
    private final StatisticsClient statisticsClient;

    //private methods
    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEventsByUserId(Long userId, int from, int size) {
        log.info("Service layer: GET /users/{userId}/events request for user with id: {} obtained.", userId);

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            String warning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        return eventRepository.findEventsByInitiator(userFound, PageRequest.of(from / size, size)).toList();
    }

    @Transactional
    @Override
    public Event createNewEventByUser(Long userId, Event event) {
        log.info("Service layer: POST /users/{userId}/events request for user with id: {} obtained.", userId);
        String warning;

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            warning = "User with id: " + event.getCategory().getId() + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        Category categoryFound = categoryRepository.findCategoryById(event.getCategory().getId());

        if (categoryFound == null) {
            warning = "Category with id: " + event.getCategory().getId() + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            warning = "Start time of event should be at least 2 hours later than now.";
            throw new IncorrectDataException(warning);
        }

        if (event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0L);
        }

        if (event.getLat() == null) {
            event.setLat(0F);
        }

        if (event.getLon() == null) {
            event.setLon(0F);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEventByUserIdAndEventId(Long userId, Long eventId) {
        log.info("Service layer: GET /users/{userId}/events/{eventId} request for user with id: {} and event " +
                "with id: {} obtained.", eventId, userId);

        String message;

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            message = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        return eventFound;
    }

    @Transactional
    @Override
    public Event updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Service layer: PATCH /users/{userId}/events/{eventId} request for user with id: {} and event with" +
                " id: {} obtained.", eventId, userId);

        String message;

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            message = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (eventFound.getState().equals(State.PUBLISHED)) {
            message = "Impossible to edit published event.";
            throw new IncorrectOperationException(message);
        }

        if (!eventFound.getInitiator().getId().equals(userId)) {
            message = "Impossible to edit not your own event.";
            throw new ForbiddenOperationException(message);
        }

        if (updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            message = "Start time of event should be 2 hours later than now.";
            throw new ForbiddenOperationException(message);
        }

        if (updateEventUserRequest.getLocation() != null) {
            eventFound.setLon(updateEventUserRequest.getLocation().getLon());
            eventFound.setLat(updateEventUserRequest.getLocation().getLat());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
                eventFound.setState(State.PENDING);
            }
            if (updateEventUserRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
                eventFound.setState(State.CANCELED);
            }
        }

        if (eventFound.getState() != State.CANCELED) {
            if (updateEventUserRequest.getAnnotation() != null) {
                eventFound.setAnnotation(updateEventUserRequest.getAnnotation());
            }

            if (updateEventUserRequest.getCategory() != null) {
                eventFound.setCategory(categoryRepository.findCategoryById(updateEventUserRequest.getCategory()));
            }

            if (updateEventUserRequest.getPaid() != null) {
                eventFound.setPaid(updateEventUserRequest.getPaid());
            }

            if (updateEventUserRequest.getEventDate() != null && !updateEventUserRequest.getEventDate()
                    .equals(eventFound.getEventDate())) {
                eventFound.setEventDate(updateEventUserRequest.getEventDate());
            }

            if (updateEventUserRequest.getDescription() != null) {
                eventFound.setDescription(updateEventUserRequest.getDescription());
            }

            if (updateEventUserRequest.getTitle() != null) {
                eventFound.setTitle(updateEventUserRequest.getTitle());
            }

            if (updateEventUserRequest.getParticipantLimit() != null) {
                eventFound.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
            }
        }

        return eventRepository.save(eventFound);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Request> getAllRequestsToEventsByEventAndUser(Long userId, Long eventId) {
        log.info("Service layer: GET /users/{userId}/events/{eventId}/requests request for user with id: {} and " +
                "event with id: {} obtained.", eventId, userId);
        String message;

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            message = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (!eventFound.getInitiator().getId().equals(userId)) {
            message = "Impossible to view participants not your event.";
            throw new ForbiddenOperationException(message);
        }

        return requestRepository.findAllByEvent(eventFound);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusOfRequests(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequest
                                                                         eventRequestStatusUpdateRequest) {
        log.info("Service layer: PATCH /users/{userId}/events/{eventId}/requests request for user with id: {} " +
                "and event with id: {} obtained.", userId, eventId);
        String message;

        User userFound = userRepository.findUserById(userId);

        if (userFound == null) {
            message = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (!eventFound.getInitiator().getId().equals(userId)) {
            message = "Impossible to edit not your own event.";
            throw new ForbiddenOperationException(message);
        }

        if (requestRepository.getCountOfConfirmedRequests(eventId) >= eventFound.getParticipantLimit()) {
            message = "Impossible to exceed limit of participants in event.";
            throw new IncorrectOperationException(message);
        }

        if (eventFound.getParticipantLimit() == 0 || !eventFound.getRequestModeration()) {
            message = "Impossible to take place in this event.";
            throw new ForbiddenOperationException(message);
        }

        List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        if (!eventRequestStatusUpdateRequest.getStatus().equals(Status.CONFIRMED)) {
            List<Request> requestsToSave = requests
                    .stream()
                    .peek(e -> {
                        if (e.getStatus().equals(Status.CONFIRMED)) {
                            String msg = "Impossible to reject approved request.";
                            throw new IncorrectOperationException(msg);
                        }

                        e.setStatus(Status.REJECTED);
                    })
                    .collect(Collectors.toList());

            return new EventRequestStatusUpdateResult(new ArrayList<>(), requestsToSave
                    .stream()
                    .map(requestDtoMapper::requestToParticipationRequestDto)
                    .collect(Collectors.toList()));
        } else {
            List<Request> requestsToSave = requests
                    .stream()
                    .peek(e -> e.setStatus(Status.CONFIRMED))
                    .collect(Collectors.toList());

            eventFound.setConfirmedRequests((long) requestsToSave.size());
            eventRepository.save(eventFound);

            return new EventRequestStatusUpdateResult(requestsToSave.stream()
                    .map(requestDtoMapper::requestToParticipationRequestDto)
                    .collect(Collectors.toList()), new ArrayList<>());
        }

    }

    //admin methods

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                           String rangeStart, String rangeEnd, int from, int size) {
        log.info("Service layer: GET /admin/events request obtained.");

        String message;

        LocalDateTime startOfRange;
        LocalDateTime endOfRange;

        if (rangeStart != null) {
            String decodedRangeStart = URLDecoder.decode(rangeStart, Charset.defaultCharset());

            try {
                startOfRange = LocalDateTime.parse(decodedRangeStart, DT_FORMATTER);
            } catch (DateTimeParseException e) {
                message = "Incorrect rangeStart value.";
                throw new IncorrectDataException(message);
            }
        } else {
            startOfRange = LocalDateTime.now();
        }

        if (rangeEnd != null) {
            String decodedRangeEnd = URLDecoder.decode(rangeEnd, Charset.defaultCharset());

            try {
                endOfRange = LocalDateTime.parse(decodedRangeEnd, DT_FORMATTER);
            } catch (DateTimeParseException e) {
                message = "Incorrect rangeEnd value.";
                throw new IncorrectDataException(message);
            }
        } else {
            endOfRange = LocalDateTime.now().plusYears(1L);
        }

        return eventRepository.getAllEventsByAdmin(users, states, categories, startOfRange, endOfRange,
                PageRequest.of(from / size, size));
    }

    @Transactional
    @Override
    public Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Service layer: PATCH /admin/events/{eventId} request for event with id : {} obtained.", eventId);
        LocalDateTime ndtm = LocalDateTime.now();
        String message;

        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id: " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate()
                .isBefore(ndtm.plusHours(1L))) {
            message = "Start time of event should be at least 1 hour later than now datetime.";
            throw new IncorrectDataException(message);
        }

        if ((eventFound.getState() == State.PUBLISHED) || (eventFound.getState() == State.CANCELED)) {
            message = "Only pending events could be changed.";
            throw new IncorrectOperationException(message);
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category categoryFound = categoryRepository.findCategoryById(updateEventAdminRequest.getCategory());

            if (categoryFound == null) {
                message = "Category with id: " + updateEventAdminRequest.getCategory() + " doesn't exist in database.";
                throw new EntityDoesNotExistException(message);
            }

            eventFound.setCategory(categoryFound);
        }

        if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
            eventFound.setState(State.CANCELED);
        }

        if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            eventFound.setState(State.PUBLISHED);
        }

        if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (updateEventAdminRequest.getAnnotation() != null) {
                eventFound.setAnnotation(updateEventAdminRequest.getAnnotation());
            }

            if (updateEventAdminRequest.getParticipantLimit() != null) {
                eventFound.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
            }

            if (updateEventAdminRequest.getEventDate() != null) {
                eventFound.setEventDate(updateEventAdminRequest.getEventDate());
            }

            if (updateEventAdminRequest.getLocation() != null) {
                eventFound.setLon(updateEventAdminRequest.getLocation().getLon());
                eventFound.setLat(updateEventAdminRequest.getLocation().getLat());
            }

            if (updateEventAdminRequest.getPaid() != null) {
                eventFound.setPaid(updateEventAdminRequest.getPaid());
            }

            if (updateEventAdminRequest.getTitle() != null) {
                eventFound.setTitle(updateEventAdminRequest.getTitle());
            }

            if (updateEventAdminRequest.getDescription() != null) {
                eventFound.setDescription(updateEventAdminRequest.getDescription());
            }

            eventFound.setPublishedOn(LocalDateTime.now());
        }

        return eventRepository.save(eventFound);
    }


    //public methods
    @Transactional
    @Override
    public List<Event> getAllPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, boolean onlyAvailable, int from,
                                          int size, HttpServletRequest httpServletRequest) {
        log.info("Service layer: GET /events request obtained.");
        String message;

        LocalDateTime startOfRange;
        LocalDateTime endOfRange;

        if (rangeStart != null) {
            String decodedStart = URLDecoder.decode(rangeStart, Charset.defaultCharset());
            try {
                startOfRange = LocalDateTime.parse(decodedStart, DT_FORMATTER);
            } catch (DateTimeParseException e) {
                message = "Incorrect rangeStart value.";
                throw new IncorrectDataException(message);
            }
        } else {
            startOfRange = LocalDateTime.now();
        }

        if (rangeEnd != null) {
            String decodedEnd = URLDecoder.decode(rangeEnd, Charset.defaultCharset());
            try {
                endOfRange = LocalDateTime.parse(decodedEnd, DT_FORMATTER);
            } catch (DateTimeParseException e) {
                message = "Incorrect rangeEnd value.";
                throw new IncorrectDataException(message);
            }
        } else {
            endOfRange = LocalDateTime.now().plusYears(1L);
        }

        if (endOfRange.isBefore(startOfRange)) {
            message = "End must be after start.";
            throw new IncorrectDataException(message);
        }

        List<Event> selectedEvents = eventRepository.getEventsFiltered(text, categories, paid, startOfRange,
                endOfRange, PageRequest.of(from / size, size));

        if (onlyAvailable) {
            selectedEvents.removeIf(e -> requestRepository.getCountOfConfirmedRequests(e.getId())
                    >= e.getParticipantLimit());
        }

        saveHit(statisticsClient, httpServletRequest);

        return selectedEvents;
    }

    @Transactional
    @Override
    public Event getFullInfoAboutPublishedEvent(Long eventId, HttpServletRequest httpServletRequest) {
        log.info("Service layer: GET /events/{id} request for event with id: {} obtained.", eventId);
        String message;
        Event eventFound = eventRepository.findEventById(eventId);

        if (eventFound == null) {
            message = "Event with id " + eventId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }

        if (!eventFound.getState().equals(State.PUBLISHED)) {
            message = "Event with id: " + eventId + " not published yet.";
            throw new EntityDoesNotExistException(message);
        }

        saveHit(statisticsClient, httpServletRequest);

        eventFound.setConfirmedRequests(requestRepository.getCountOfConfirmedRequests(eventFound.getId()));

        return eventFound;
    }

    @Transactional
    private void saveHit(StatisticsClient statisticsClient, HttpServletRequest httpServletRequest) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("ewm-main");
        hitDto.setUri(httpServletRequest.getRequestURI());
        hitDto.setIp(httpServletRequest.getRemoteAddr());
        hitDto.setTimestamp(LocalDateTime.now());

        statisticsClient.saveStatistics(hitDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Long> getStatisticsOfViews(List<Event> events) {
        String pathElem = "/events/";
        String serviceName = "ewm-main";

        List<String> uris = events
                .stream()
                .map(Event::getId)
                .map(id -> pathElem + id.toString())
                .collect(Collectors.toList());

        List<StatisticsDto> views = statisticsClient.getStatistics(LocalDateTime.now().minusMonths(1L),
                LocalDateTime.now().plusMonths(1L), uris, true);

        return views
                .stream()
                .filter(e -> e.getApp().equals(serviceName))
                .collect(Collectors.toMap(e -> parseEventId(e.getUri()), StatisticsDto::getHits)
                );
    }

    private Long parseEventId(String str) {

        return Long.parseLong(str.substring(str.lastIndexOf('/') +
                1));
    }

    @Transactional
    @Override
    public Event getEventById(Long eventId) {

        return eventRepository.findEventById(eventId);
    }
}
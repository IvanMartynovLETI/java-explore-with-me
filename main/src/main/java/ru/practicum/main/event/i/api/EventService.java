package ru.practicum.main.event.i.api;

import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.model.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EventService {
    //private methods
    List<Event> getAllEventsByUserId(Long userId, int from, int size);

    Event createNewEventByUser(Long userId, Event event);

    Event getEventByUserIdAndEventId(Long userId, Long eventId);

    Event updateEventByUser(Long userId, Long EventId, UpdateEventUserRequest updateEventUserRequest);

    List<Request> getAllRequestsToEventsByEventAndUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusOfRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest
            eventRequestStatusUpdateRequest);

    //admin methods
    Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<Event> getAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                    String rangeStart, String rangeEnd, int from, int size);

    //public methods
    List<Event> getAllPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                   String rangeEnd, boolean onlyAvailable, int from, int size,
                                   HttpServletRequest httpServletRequest);

    Event getFullInfoAboutPublishedEvent(Long eventId, HttpServletRequest httpServletRequest);

    Map<Long, Long> getStatisticsOfViews(List<Event> events);

    //common use methods
    Event getEventById(Long eventId);
}
package ru.practicum.main.event.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.category.dto.CategoryDtoMapper;
import ru.practicum.main.category.i.api.CategoryService;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.user.dto.UserDtoMapper;
import ru.practicum.main.user.i.api.UserService;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventDtoMapper {
    private final CategoryDtoMapper categoryDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final UserService userService;
    private final CategoryService categoryService;

    public EventShortDto eventToEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(categoryDtoMapper.categoryToCategoryDto(event.getCategory()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(userDtoMapper.userToUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());

        return eventShortDto;
    }

    public List<EventShortDto> eventsToEventShortDtos(List<Event> events) {

        return events
                .stream()
                .map(this::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    public Event newEventDtoToEvent(NewEventDto newEventDto, Long userId) {
        Event event = new Event();

        User userFound = userService.getUserById(userId);

        if (userFound == null) {
            String warning = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(warning);
        }

        event.setCategory(categoryService.getCategoryById(newEventDto.getCategory()));
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setInitiator(userFound);
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setPaid(newEventDto.getPaid());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(newEventDto.getEventDate());

        return event;
    }

    public EventFullDto eventToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryDtoMapper.categoryToCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(userDtoMapper.userToUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());

        return eventFullDto;
    }

    public List<EventFullDto> eventsToEventFullDtos(List<Event> events) {

        return events
                .stream()
                .map(this::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getSortedShortDtos(List<Event> events, Map<Long, Long> statistics,
                                                  String sort) {
        Stream<EventShortDto> shortDtosStream = events
                .stream()
                .map(this::eventToEventShortDto)
                .peek(e -> e.setViews(statistics.get(e.getId())));

        if (sort.equals("VIEWS")) {
            return shortDtosStream
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        } else {
            return shortDtosStream
                    .sorted(Comparator.comparing(EventShortDto::getEventDate).reversed())
                    .collect(Collectors.toList());
        }
    }

    public EventFullDto eventToEventFullDtoWithViews(Event event, Map<Long, Long> statistics) {
        EventFullDto eventFullDto = eventToEventFullDto(event);

        if (!statistics.isEmpty()) {
            eventFullDto.setViews(statistics.get(event.getId()));
        }

        return eventFullDto;
    }
}
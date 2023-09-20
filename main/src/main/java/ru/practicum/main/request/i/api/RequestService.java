package ru.practicum.main.request.i.api;

import ru.practicum.main.request.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> getAllRequestsByUserId(Long userId);

    Request saveRequest(Long userId, Long eventId);

    Request updateRequest(Long userId, Long requestId);
}
package ru.practicum.main.request.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.dto.Status;
import ru.practicum.main.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findRequestsByRequesterId(Long requesterId);

    Request findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventAndStatus(Event event, Status status);


    List<Request> findAllByEvent(Event event);

    @Query("SELECT COUNT(R.id) FROM Request AS R WHERE R.event.id = :eventId AND R.status = 'CONFIRMED'")
    Long getCountOfConfirmedRequests(Long eventId);
}
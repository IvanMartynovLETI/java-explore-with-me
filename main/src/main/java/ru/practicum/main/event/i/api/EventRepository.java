package ru.practicum.main.event.i.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findEventById(Long eventId);

    Page<Event> findEventsByInitiator(User user, Pageable pageable);

    @Query("SELECT E FROM Event AS E WHERE E.initiator.id IN :users OR (E.initiator) IS NOT NULL AND (E.category.id " +
            "IN :categories OR (:categories) IS NULL) AND (E.state IN :states OR (:states) IS NULL) AND E.eventDate " +
            "BETWEEN :rangeStart AND :rangeEnd ORDER BY E.id DESC")
    List<Event> getAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    @Query("SELECT E FROM Event AS E WHERE (LOWER(E.description) LIKE LOWER(CONCAT('%',:text,'%')) OR " +
            "LOWER(E.annotation) LIKE LOWER(CONCAT('%',:text,'%')) OR :text IS NULL) AND (E.category.id IN " +
            "(:categories) OR (:categories) IS NULL) AND (E.paid = :paid OR (:paid) IS NULL) AND E.eventDate BETWEEN" +
            " :rangeStart AND :rangeEnd")
    List<Event> getEventsFiltered(String text, List<Long> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    List<Event> findEventsByCategoryId(Long categoryId);
}
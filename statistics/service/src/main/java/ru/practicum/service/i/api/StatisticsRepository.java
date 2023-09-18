package ru.practicum.service.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT H.app AS app, H.uri AS uri, COUNT (H.id) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN" +
            " :start AND :end) AND H.uri IN (:uris) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getStatisticsWithUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("SELECT H.app AS app, H.uri AS uri, COUNT (distinct H.ip) AS hits FROM Hit AS H WHERE (H.timestamp " +
            "BETWEEN :start AND :end) AND H.uri IN (:uris) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getUniqueStatisticsWithUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("SELECT H.app AS app, H.uri AS uri, COUNT (H.id) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN " +
            ":start AND :end) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getStatisticsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT H.app AS app, H.uri AS uri, COUNT (distinct H.ip) AS hits FROM Hit AS H WHERE (H.timestamp " +
            "BETWEEN :start AND :end) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getUniqueStatisticsWithoutUris(LocalDateTime start, LocalDateTime end);
}
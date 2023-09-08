package ru.practicum.service.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.model.Hit;

import java.util.Collection;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT H.app, H.uri, COUNT (H.id) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN cast(:start as" +
            " timestamp ) AND cast(:end as timestamp )) AND H.uri IN (:uris) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getStatisticsWithUris(String start, String end, Collection<String> uris);

    @Query("SELECT H.app, H.uri, COUNT (distinct H.ip) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN cast(:start " +
            "as timestamp) AND cast(:end as timestamp)) AND H.uri IN (:uris) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getUniqueStatisticsWithUris(String start, String end, Collection<String> uris);

    @Query("SELECT H.app, H.uri, COUNT (H.id) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN cast(:start as" +
            " timestamp) AND cast(:end as timestamp)) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getStatisticsWithoutUris(String start, String end);

    @Query("SELECT H.app, H.uri, COUNT (distinct H.ip) AS hits FROM Hit AS H WHERE (H.timestamp BETWEEN cast(:start " +
            "as timestamp) AND cast(:end as timestamp)) GROUP BY H.app, H.uri ORDER BY hits DESC")
    List<ViewStats> getUniqueStatisticsWithoutUris(String start, String end);
}
package ru.practicum.service.i.api;

import ru.practicum.service.model.ViewStats;
import ru.practicum.service.model.Hit;

import java.util.Collection;
import java.util.List;

public interface StatisticsService {
    Hit saveStatistics(Hit hitEndpoint);

    Collection<ViewStats> getStatistics(String encodedStart, String encodedEnd, List<String> encodedUris,
                                        Boolean unique);
}

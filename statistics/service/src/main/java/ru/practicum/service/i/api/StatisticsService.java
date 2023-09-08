package ru.practicum.service.i.api;

import ru.practicum.dto.StatisticsDto;
import ru.practicum.service.model.Hit;

import java.util.List;

public interface StatisticsService {
    Hit saveStatistics(Hit hitEndpoint);

    List<StatisticsDto> getStatistics(String encodedStart, String encodedEnd, List<String> encodedUris,
                                      Boolean unique);
}

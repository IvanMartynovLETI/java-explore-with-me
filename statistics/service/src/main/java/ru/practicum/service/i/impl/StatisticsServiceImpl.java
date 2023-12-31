package ru.practicum.service.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatisticsDto;
import ru.practicum.service.exception.IncorrectSearchParametersException;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.i.api.StatisticsRepository;
import ru.practicum.service.i.api.StatisticsService;
import ru.practicum.service.model.Hit;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Transactional
    @Override
    public Hit saveStatistics(Hit hit) {
        log.info("Service layer: request for app: {} from user with ip: {}.", hit.getApp(), hit.getIp());

        return statisticsRepository.save(hit);
    }

    @Override
    public List<StatisticsDto> getStatistics(String encodedStart, String encodedEnd, List<String> encodedUris,
                                             Boolean unique) {
        String decodedStart;
        String decodedEnd;
        List<String> decodedUris;
        List<ViewStats> viewStats;
        LocalDateTime startOfSearch;
        LocalDateTime endOfSearch;


        if (encodedStart != null && encodedEnd == null) {
            throw new IncorrectSearchParametersException("End time must be non-null.");
        }

        if ((encodedStart == null) && encodedEnd != null) {
            throw new IncorrectSearchParametersException("Start time must be non-null.");
        }

        if (encodedStart == null & encodedEnd == null) {
            throw new IncorrectSearchParametersException("Start and End must be non-null.");
        }

        decodedStart = URLDecoder.decode(encodedStart, Charset.defaultCharset());

        decodedEnd = URLDecoder.decode(encodedEnd, Charset.defaultCharset());

        try {
            startOfSearch = LocalDateTime.parse(decodedStart, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IncorrectSearchParametersException("Incorrect start time.");
        }

        try {
            endOfSearch = LocalDateTime.parse(decodedEnd, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IncorrectSearchParametersException("Incorrect end time.");
        }

        if (endOfSearch.isBefore(startOfSearch)) {
            throw new IncorrectSearchParametersException("Start time must be before End time.");
        }

        if (encodedUris != null) {
            decodedUris = encodedUris.stream()
                    .map(uri -> URLDecoder.decode(uri, Charset.defaultCharset()))
                    .collect(Collectors.toList());
        } else {
            decodedUris = null;
        }

        if (unique && decodedUris != null) {
            viewStats = statisticsRepository.getUniqueStatisticsWithUris(decodedStart, decodedEnd, decodedUris);
        } else if (unique) {
            viewStats = statisticsRepository.getUniqueStatisticsWithoutUris(decodedStart, decodedEnd);
        } else if (decodedUris != null) {
            viewStats = statisticsRepository.getStatisticsWithUris(decodedStart, decodedEnd, decodedUris);
        } else {
            viewStats = statisticsRepository.getStatisticsWithoutUris(decodedStart, decodedEnd);
        }

        return viewStats
                .stream()
                .map(e -> new StatisticsDto(e.getApp(), e.getUri(), e.getHits()))
                .collect(Collectors.toList());
    }
}
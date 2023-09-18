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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        if (encodedStart != null) {
            decodedStart = URLDecoder.decode(encodedStart, Charset.defaultCharset());
            startOfSearch = LocalDateTime.parse(decodedStart, DATE_TIME_FORMATTER);
        } else {
            startOfSearch = LocalDateTime.now().minusMonths(1L);
        }

        if (encodedEnd != null) {
            decodedEnd = URLDecoder.decode(encodedEnd, Charset.defaultCharset());
            endOfSearch = LocalDateTime.parse(decodedEnd, DATE_TIME_FORMATTER);
        } else {
            endOfSearch = LocalDateTime.now().plusMonths(1L);
        }

        if (endOfSearch.isBefore(startOfSearch)) {
            throw new IncorrectSearchParametersException("End time must be after start time.");
        }

        if (encodedUris != null) {
            decodedUris = encodedUris.stream()
                    .map(uri -> URLDecoder.decode(uri, Charset.defaultCharset()))
                    .collect(Collectors.toList());
        } else {
            decodedUris = null;
        }

        if (unique && decodedUris != null) {
            viewStats = statisticsRepository.getUniqueStatisticsWithUris(startOfSearch, endOfSearch, decodedUris);
        } else if (unique) {
            viewStats = statisticsRepository.getUniqueStatisticsWithoutUris(startOfSearch, endOfSearch);
        } else if (decodedUris != null) {
            viewStats = statisticsRepository.getStatisticsWithUris(startOfSearch, endOfSearch, decodedUris);
        } else {
            viewStats = statisticsRepository.getStatisticsWithoutUris(startOfSearch, endOfSearch);
        }

        return viewStats
                .stream()
                .map(e -> new StatisticsDto(e.getApp(), e.getUri(), e.getHits()))
                .collect(Collectors.toList());
    }
}
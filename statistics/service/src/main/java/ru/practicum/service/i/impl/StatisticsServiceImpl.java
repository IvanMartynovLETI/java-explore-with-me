package ru.practicum.service.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.i.api.StatisticsRepository;
import ru.practicum.service.i.api.StatisticsService;
import ru.practicum.service.model.Hit;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository repository;

    @Transactional
    @Override
    public Hit saveStatistics(Hit hit) {
        log.info("Service layer: request for app: {} from user with ip: {}.", hit.getApp(), hit.getIp());

        return repository.save(hit);
    }

    @Override
    public Collection<ViewStats> getStatistics(String encodedStart, String encodedEnd, List<String> encodedUris,
                                               Boolean unique) {
        String decodedStart;
        String decodedEnd;
        List<String> decodedUris;

        if (encodedStart != null) {
            decodedStart = URLDecoder.decode(encodedStart, Charset.defaultCharset());
        } else {
            decodedStart = null;
        }

        if (encodedEnd != null) {
            decodedEnd = URLDecoder.decode(encodedEnd, Charset.defaultCharset());
        } else {
            decodedEnd = null;
        }

        if (encodedUris != null) {
            decodedUris = encodedUris.stream()
                    .map(uri -> URLDecoder.decode(uri, Charset.defaultCharset()))
                    .collect(Collectors.toList());
        } else {
            decodedUris = null;
        }

        if (unique && decodedUris != null) {
            return repository.getUniqueStatisticsWithUris(decodedStart, decodedEnd, decodedUris);
        } else if (unique) {
            return repository.getUniqueStatisticsWithoutUris(decodedStart, decodedEnd);
        } else if (decodedUris != null) {
            return repository.getStatisticsWithUris(decodedStart, decodedEnd, decodedUris);
        } else {
            return repository.getStatisticsWithoutUris(decodedStart, decodedEnd);
        }
    }
}
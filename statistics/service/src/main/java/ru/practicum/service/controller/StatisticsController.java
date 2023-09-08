package ru.practicum.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticsDto;
import ru.practicum.service.mapper.HitDtoMapper;
import ru.practicum.service.i.api.StatisticsService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService service;
    private final HitDtoMapper hitDtoMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveStatistics(@RequestBody HitDto hitDto) {
        log.info("Controller layer: request for statistics save obtained.");

        return hitDtoMapper.hitToHitDto(service.saveStatistics(hitDtoMapper.hitDtoToHit(hitDto)));
    }

    @GetMapping("/stats")
    public List<StatisticsDto> getStatistics(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "False") Boolean unique) {
        log.info("Controller layer: request for getting statistics obtained.");

        return service.getStatistics(start, end, uris, unique);
    }
}
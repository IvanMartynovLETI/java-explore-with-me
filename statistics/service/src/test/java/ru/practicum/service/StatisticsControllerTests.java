package ru.practicum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticsDto;
import ru.practicum.service.controller.StatisticsController;
import ru.practicum.service.i.api.StatisticsService;
import ru.practicum.service.mapper.HitDtoMapper;
import ru.practicum.service.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticsController.class)
public class StatisticsControllerTests {
    private HitDto hitDto;
    private Hit hit;
    private StatisticsDto statisticsDto;
    private List<StatisticsDto> listOfStatistics;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatisticsService service;

    @MockBean
    HitDtoMapper hitDtoMapper;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    public void beforeEach() {
        hitDto = new HitDto(1L, "ewm-main-service", "events/1", "192.168.1.1",
                LocalDateTime.of(2023, 4, 5, 12, 32, 11));
        hit = new Hit(1L, "ewm-main-service", "events/1", "192.168.1.1",
                LocalDateTime.of(2023, 4, 5, 12, 32, 11));
        statisticsDto = new StatisticsDto("ewm-main-service", "events/1", 36L);
        listOfStatistics = List.of(statisticsDto);
    }

    @SneakyThrows
    @Test
    public void saveStatisticsTest() {
        when(hitDtoMapper.hitDtoToHit(any()))
                .thenReturn(hit);

        when(service.saveStatistics(any()))
                .thenReturn(hit);

        when(hitDtoMapper.hitToHitDto(any()))
                .thenReturn(hitDto);

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(hitDto.getId()))
                .andExpect(jsonPath("$.app").value(hitDto.getApp()))
                .andExpect(jsonPath("$.uri").value(hitDto.getUri()))
                .andExpect(jsonPath("$.ip").value(hitDto.getIp()))
                .andExpect(jsonPath("$.timestamp").value(hitDto.getTimestamp().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    @SneakyThrows
    @Test
    public void getStatisticsTest() {
        when(service.getStatistics(any(), any(), any(), any()))
                .thenReturn(listOfStatistics);

        mvc.perform(get("/stats")
                        .param("start", "2023-09-04%2012%3A32%3A41")
                        .param("end", "2023-09-04%2018%3A32%3A41")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(statisticsDto.getApp()))
                .andExpect(jsonPath("$[0].uri").value(statisticsDto.getUri()))
                .andExpect(jsonPath("$[0].hits").value(statisticsDto.getHits()));
    }
}
package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.service.i.api.StatisticsRepository;
import ru.practicum.service.i.impl.StatisticsServiceImpl;
import ru.practicum.service.model.Hit;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {
    private final Hit hit = new Hit(1L, "ewm-main-service", "events/1", "192.168.1.1",
            LocalDateTime.of(2023, 4, 5, 12, 32, 11));

    @InjectMocks
    private StatisticsServiceImpl service;

    @Mock
    private StatisticsRepository repository;

    @Test
    public void saveStatisticsTest() {
        when(repository.save(any())).thenReturn(hit);

        assertEquals(hit, service.saveStatistics(hit));
    }
}
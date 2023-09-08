package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.dto.StatisticsDto;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class StatisticsDtoTest {
    @Autowired
    JacksonTester<StatisticsDto> json;

    @Test
    public void serializationTest() throws Exception {
        StatisticsDto statisticsDto = new StatisticsDto("ewm-main-service", "events/1", 21L);
        JsonContent<StatisticsDto> jsonContent = json.write(statisticsDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo(statisticsDto.getApp());
        assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo(statisticsDto.getUri());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.hits").isEqualTo(21);
    }
}

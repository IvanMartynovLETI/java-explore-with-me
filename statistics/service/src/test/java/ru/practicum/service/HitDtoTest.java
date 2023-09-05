package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class HitDtoTest {
    @Autowired
    JacksonTester<HitDto> json;

    @Test
    public void serializationTest() throws Exception {
        HitDto hitDto = new HitDto(1L, "ewm-main-service", "events/1", "192.168.1.1",
                LocalDateTime.of(2023, 4, 5, 12, 32, 11));
        JsonContent<HitDto> jsonContent = json.write(hitDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo(hitDto.getApp());
        assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo(hitDto.getUri());
        assertThat(jsonContent).extractingJsonPathStringValue("$.ip").isEqualTo(hitDto.getIp());
        assertThat(jsonContent).extractingJsonPathStringValue("$.timestamp")
                .isEqualTo(hitDto.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
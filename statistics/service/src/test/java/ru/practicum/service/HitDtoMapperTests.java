package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.dto.HitDto;
import ru.practicum.service.mapper.HitDtoMapper;
import ru.practicum.service.model.Hit;

import java.time.LocalDateTime;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HitDtoMapperTests {
    private final HitDtoMapper hitDtoMapper;
    private static Hit hit1;
    private static HitDto hitDto;

    @BeforeAll
    public static void beforeAll() {
        hit1 = new Hit(1L, "ewm-main-service", "events/1", "192.168.1.1",
                LocalDateTime.of(2023, 4, 5, 12, 32, 11));

        hitDto = new HitDto(1L, "ewm-main-service", "events/1", "192.168.1.1",
                LocalDateTime.of(2023, 4, 5, 12, 32, 11));
    }

    @Test
    public void hitToHitDtoTest() {
        hitDto = hitDtoMapper.hitToHitDto(hit1);

        assertThat(hitDto.getId()).isEqualTo(hit1.getId());
        assertThat(hitDto.getApp()).isEqualTo(hit1.getApp());
        assertThat(hitDto.getUri()).isEqualTo(hit1.getUri());
        assertThat(hitDto.getIp()).isEqualTo(hit1.getIp());
        assertThat(hitDto.getTimestamp()).isEqualTo(hit1.getTimestamp());
    }

    @Test
    public void hitDtoToHitTest() {
        Hit hit2 = hitDtoMapper.hitDtoToHit(hitDto);

        assertThat(hit2.getId()).isEqualTo(hitDto.getId());
        assertThat(hit2.getApp()).isEqualTo(hitDto.getApp());
        assertThat(hit2.getUri()).isEqualTo(hitDto.getUri());
        assertThat(hit2.getIp()).isEqualTo(hitDto.getIp());
        assertThat(hit2.getTimestamp()).isEqualTo(hitDto.getTimestamp());
    }
}
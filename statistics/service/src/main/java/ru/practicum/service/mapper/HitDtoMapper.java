package ru.practicum.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;
import ru.practicum.service.model.Hit;

@Component
public class HitDtoMapper {
    public Hit hitDtoToHit(HitDto hitDto) {

        return new Hit(hitDto.getId(), hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
    }

    public HitDto hitToHitDto(Hit hit) {

        return new HitDto(hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}
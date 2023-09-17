package ru.practicum.main.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.main.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestDtoMapper {
    public ParticipationRequestDto requestToParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(request.getStatus());
        participationRequestDto.setCreated(request.getCreated());

        return participationRequestDto;
    }

    public List<ParticipationRequestDto> requestsToDtos(List<Request> requestList) {

        return requestList
                .stream()
                .map(this::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
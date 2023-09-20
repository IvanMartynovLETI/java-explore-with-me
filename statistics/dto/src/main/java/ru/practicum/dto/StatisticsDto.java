package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatisticsDto {
    private String app;
    private String uri;
    private Long hits;
}
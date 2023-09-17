package ru.practicum.main.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
}
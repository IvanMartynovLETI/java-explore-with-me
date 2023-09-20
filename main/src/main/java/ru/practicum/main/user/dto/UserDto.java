package ru.practicum.main.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String email;
}
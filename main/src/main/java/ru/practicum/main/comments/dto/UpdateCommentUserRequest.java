package ru.practicum.main.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentUserRequest {
    @NotBlank
    @Size(min = 3, max = 10200)
    String text;
}

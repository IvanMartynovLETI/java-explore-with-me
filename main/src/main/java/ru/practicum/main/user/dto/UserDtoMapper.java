package ru.practicum.main.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.main.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {
    public UserDto userToUserDto(User user) {

        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User newUserRequestToUser(NewUserRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());

        return user;
    }

    public UserShortDto userToUserShortDto(User user) {

        return new UserShortDto(user.getId(), user.getName());
    }

    public List<UserDto> usersToDtos(List<User> users) {

        return users
                .stream()
                .map(this::userToUserDto)
                .collect(Collectors.toList());
    }
}
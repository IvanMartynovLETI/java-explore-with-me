package ru.practicum.main.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserDtoMapper;
import ru.practicum.main.user.i.api.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Controller layer: GET /admin/users request obtained.");

        return userDtoMapper.usersToDtos(userService.getAllUsers(ids, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Controller layer: POST /admin/users request for adding user with name: {} obtained.",
                newUserRequest.getName());

        return userDtoMapper.userToUserDto(userService.saveUser(userDtoMapper.newUserRequestToUser(newUserRequest)));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Controller layer: DELETE /admin/users/{userId} request for deleting user with id: {} obtained.",
                userId);

        userService.deleteUserById(userId);
    }
}
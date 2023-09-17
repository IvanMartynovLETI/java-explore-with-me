package ru.practicum.main.user.i.api;

import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    List<User> getAllUsers(List<Long> userIds, int from, int size);

    void deleteUserById(Long userId);

    User getUserById(Long userId);
}
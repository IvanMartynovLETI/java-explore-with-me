package ru.practicum.main.user.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.EntityDoesNotExistException;
import ru.practicum.main.user.i.api.UserRepository;
import ru.practicum.main.user.i.api.UserService;
import ru.practicum.main.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User saveUser(User user) {
        log.info("Controller layer: POST /admin/users request for adding user with name: {} obtained.",
                user.getName());

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers(List<Long> userIds, int from, int size) {
        log.info("Service layer: GET /admin/users request obtained.");

        if (userIds == null) {
            return userRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            return userRepository.findByIdIn(userIds, PageRequest.of(from / size, size));
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        log.info("Controller layer: DELETE /admin/users/{userId} request for deleting user with id: {} obtained.",
                userId);

        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            String message = "User with id: " + userId + " doesn't exist in database.";
            throw new EntityDoesNotExistException(message);
        }
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Service layer: request for getting user with id: {} obtained.", userId);

        return userRepository.findById(userId).orElseThrow(() ->
                new EntityDoesNotExistException("User with id: " + userId + " doesn't exist in database."));
    }
}
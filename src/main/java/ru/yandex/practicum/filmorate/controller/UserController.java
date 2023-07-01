package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController extends AbstractController<User> {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUserId(@PathVariable("id") Integer userId) {
        log.debug("Список друзей пользователя с ИД {}", userId);
        return userService.getFriendsByUserId(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        log.debug("Список общих друзей пользователей с ИД {} и {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendship(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.debug("Пользователь с ИД {} добавляет в друзья пользователя с ИД {}", userId, friendId);
        userService.addFriendship(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendship(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.debug("Пользователь с ИД {} удаляет из друзей пользователя с ИД {}", userId, friendId);
        userService.removeFriendship(userId, friendId);
    }

}

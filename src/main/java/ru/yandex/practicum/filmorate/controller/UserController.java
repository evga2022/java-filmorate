package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
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
        trowIfUserNotExist(userId);
        return userService.getFriendsByUserId(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendship(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(friendId);
        userService.addFriendship(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendship(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(friendId);
        userService.removeFriendship(userId, friendId);
    }

    @Override
    protected String getTitle() {
        return "пользователь";
    }

    @Override
    protected ValidationException doValidate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            return new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            return new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            return new ValidationException("Дата рождения не может быть в будущем");
        }
        return super.doValidate(user);
    }

    private void trowIfUserNotExist(Integer id) {
        if (userService.getById(id) == null) {
            log.debug("Не найден пользователь с таким ИД: {}", id);
            throw new NotFoundException();
        }
    }
}

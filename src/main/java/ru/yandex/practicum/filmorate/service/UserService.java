package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractService<User> {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        super(storage);
        this.storage = storage;
    }

    @Override
    public User create(User newUser) {
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
        }
        return super.create(newUser);
    }

    public List<User> getFriendsByUserId(Integer id) {
        trowIfUserNotExist(id);
        return storage.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(otherId);
        return storage.getCommonFriends(userId, otherId);
    }

    public void addFriendship(Integer userId, Integer friendId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(friendId);
        storage.addFriendship(userId, friendId);
    }

    public void removeFriendship(Integer userId, Integer friendId) {
        trowIfUserNotExist(userId);
        trowIfUserNotExist(friendId);
        storage.removeFriendship(userId, friendId);
    }

    @Override
    public String getTitle() {
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
        if (getById(id) == null) {
            log.debug("Не найден пользователь с таким ИД: {}", id);
            throw new NotFoundException();
        }
    }
}
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService extends AbstractService<User> {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
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
        return storage.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return storage.getCommonFriends(userId, otherId);
    }

    public void addFriendship(Integer userId, Integer friendId) {
        storage.addFriendship(userId, friendId);
    }

    public void removeFriendship(Integer userId, Integer friendId) {
        storage.removeFriendship(userId, friendId);
    }
}
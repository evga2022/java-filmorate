package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends AbstractStorage<User> {
    List<User> getFriendsByUserId(Integer id);
    List<User> getCommonFriends(Integer userId, Integer otherId);
    void addFriendship(Integer userId, Integer friendId);
    void removeFriendship(Integer userId, Integer friendId);
}

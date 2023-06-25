package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FriendsStorage {
    void addFriendship(Integer userId, Integer friendId);

    void removeFriendship(Integer userId, Integer friendId);

    void removeAllFriendFromUser(Integer userId);

    List<Integer> getAllFriendIdsByUserId(Integer userId);
}

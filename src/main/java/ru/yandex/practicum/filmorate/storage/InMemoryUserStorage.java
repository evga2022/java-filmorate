package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage extends InMemoryAbstractStorage<User> implements UserStorage {
    private final LikeStorage likeStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public InMemoryUserStorage(LikeStorage likeStorage, FriendsStorage friendsStorage) {
        this.likeStorage = likeStorage;
        this.friendsStorage = friendsStorage;
    }

    @Override
    public void delete(Integer id) {
        likeStorage.removeAllUserLikes(id);
        friendsStorage.removeAllFriendFromUser(id);
        super.delete(id);
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return friendsStorage.getAllFriendIdsByUserId(id).stream()
                .map(super::getById).map(Optional::get).sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<Integer> userFriends = friendsStorage.getAllFriendIdsByUserId(userId);
        List<Integer> otherFriends = friendsStorage.getAllFriendIdsByUserId(otherId);
        return userFriends.stream().filter(otherFriends::contains).map(super::getById)
                .map(Optional::get)
                .sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
    }

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        friendsStorage.addFriendship(userId, friendId);
    }

    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        friendsStorage.removeFriendship(userId, friendId);
    }
}

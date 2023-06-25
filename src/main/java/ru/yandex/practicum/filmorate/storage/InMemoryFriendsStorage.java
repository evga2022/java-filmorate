package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendsStorage implements FriendsStorage {

    private final Set<AbstractMap.SimpleEntry<Integer, Integer>> friendsStore = new HashSet<>();

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        friendsStore.add(new AbstractMap.SimpleEntry<>(userId, friendId));
        friendsStore.add(new AbstractMap.SimpleEntry<>(friendId, userId));
    }

    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        friendsStore.remove(new AbstractMap.SimpleEntry<>(userId, friendId));
        friendsStore.remove(new AbstractMap.SimpleEntry<>(friendId, userId));
    }

    @Override
    public void removeAllFriendFromUser(Integer userId) {
        friendsStore.removeIf(pair -> pair.getKey().equals(userId) || pair.getValue().equals(userId));
    }

    @Override
    public List<Integer> getAllFriendIdsByUserId(Integer userId) {
        return friendsStore.stream().filter(pair -> pair.getKey().equals(userId))
                .map(AbstractMap.SimpleEntry::getValue).collect(Collectors.toList());
    }
}

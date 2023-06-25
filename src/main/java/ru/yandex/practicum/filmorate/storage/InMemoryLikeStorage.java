package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryLikeStorage implements LikeStorage {

    // Здесь за ключ будет ид фильма, а за значение будет ид пользователя
    private final Set<AbstractMap.SimpleEntry<Integer, Integer>> likeStore = new HashSet<>();

    @Override
    public void addUserLikeToFilm(Integer userId, Integer filmId) {
        likeStore.add(new AbstractMap.SimpleEntry<>(filmId, userId));
    }

    @Override
    public void removeUserLikeFromFilm(Integer userId, Integer filmId) {
        likeStore.remove(new AbstractMap.SimpleEntry<>(filmId, userId));
    }

    @Override
    public void removeAllUserLikes(Integer userId) {
        likeStore.removeIf(pair -> pair.getValue().equals(userId));
    }

    @Override
    public void removeAllFilmLikes(Integer filmId) {
        likeStore.removeIf(pair -> pair.getKey().equals(filmId));
    }

    @Override
    public Map<Integer, List<Integer>> getCountsOfLikesByFilms() {
        return likeStore.stream()
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }

}

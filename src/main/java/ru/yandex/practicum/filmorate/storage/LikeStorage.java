package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface LikeStorage {
    void addUserLikeToFilm(Integer userId, Integer filmId);

    void removeUserLikeFromFilm(Integer userId, Integer filmId);

    void removeAllUserLikes(Integer userId);

    void removeAllFilmLikes(Integer filmId);

    Map<Integer, List<Integer>> getCountsOfLikesByFilms();
}

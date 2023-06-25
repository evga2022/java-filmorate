package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {
    void addUserLikeToFilm(Integer userId, Integer filmId);

    void removeUserLikeFromFilm(Integer userId, Integer filmId);

    List<Film> getFilmsByLikes(Integer from, Integer limit);
}

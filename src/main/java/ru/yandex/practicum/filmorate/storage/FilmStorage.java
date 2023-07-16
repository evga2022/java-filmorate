package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage extends AbstractStorage<Film> {
    void addUserLikeToFilm(Integer userId, Integer filmId);

    void removeUserLikeFromFilm(Integer userId, Integer filmId);

    List<Film> getFilmsByLikes(Integer from, Integer limit);

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(Integer id);

    List<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(Integer id);
}

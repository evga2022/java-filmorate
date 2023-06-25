package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService extends AbstractService<Film> {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        super(storage);
        this.storage = storage;
    }

    public void addUserLikeToFilm(Integer userId, Integer filmId) {
        storage.addUserLikeToFilm(userId, filmId);
    }

    public void removeUserLikeFromFilm(Integer userId, Integer filmId) {
        storage.removeUserLikeFromFilm(userId, filmId);
    }

    public List<Film> getFilmsByLikes(Integer from, Integer limit) {
        return storage.getFilmsByLikes(from, limit);
    }
}
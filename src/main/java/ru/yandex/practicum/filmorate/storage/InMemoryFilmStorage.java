package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.Comparators;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage extends InMemoryAbstractStorage<Film> implements FilmStorage {
    private final LikeStorage likeStorage;

    @Autowired
    public InMemoryFilmStorage(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    @Override
    public void delete(Integer id) {
        likeStorage.removeAllFilmLikes(id);
        super.delete(id);
    }

    @Override
    public void addUserLikeToFilm(Integer userId, Integer filmId) {
        likeStorage.addUserLikeToFilm(userId, filmId);
    }

    @Override
    public void removeUserLikeFromFilm(Integer userId, Integer filmId) {
        likeStorage.removeUserLikeFromFilm(userId, filmId);
    }

    @Override
    public List<Film> getFilmsByLikes(Integer from, Integer limit) {
        Map<Integer, List<Integer>> filmsByLike = likeStorage.getCountsOfLikesByFilms();
        return super.findAll().stream()
                .sorted((a, b) -> Comparators.comparable().compare(filmsByLike.getOrDefault(b.getId(),
                        new ArrayList<>()).size(), filmsByLike.getOrDefault(a.getId(), new ArrayList<>()).size()))
                .skip(from).limit(limit).collect(Collectors.toList());
    }
}

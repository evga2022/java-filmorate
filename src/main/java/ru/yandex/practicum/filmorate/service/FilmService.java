package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final UserService userService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserService userService,
                       FilmStorage filmStorage) {
        super(filmStorage);
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public void addUserLikeToFilm(Integer userId, Integer filmId) {
        trowIfUserNotExist(userId);
        trowIfFilmNotExist(filmId);
        filmStorage.addUserLikeToFilm(userId, filmId);
    }

    public void removeUserLikeFromFilm(Integer userId, Integer filmId) {
        trowIfUserNotExist(userId);
        trowIfFilmNotExist(filmId);
        filmStorage.removeUserLikeFromFilm(userId, filmId);
    }

    public List<Film> getFilmsByLikes(Integer from, Integer limit) {
        return filmStorage.getFilmsByLikes(from, limit);
    }

    @Override
    public String getTitle() {
        return "фильм";
    }

    @Override
    protected ValidationException doValidate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            return new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            return new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            return new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return super.doValidate(film);
    }

    private void trowIfUserNotExist(Integer id) {
        // Метод getById(id) выкинет исключение если не будет пользователя с таким ИД, лог тоже внутри метода getById(id)
        userService.getById(id);
    }

    private void trowIfFilmNotExist(Integer id) {
        // Метод getById(id) выкинет исключение если не будет фильма с таким ИД, лог тоже внутри метода getById(id)
        getById(id);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        Optional<Genre> result = filmStorage.getGenreById(id);
        if (result.isEmpty()) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), id);
            throw new NotFoundException();
        }
        return result.get();
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        Optional<Mpa> result = filmStorage.getMpaById(id);
        if (result.isEmpty()) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), id);
            throw new NotFoundException();
        }
        return result.get();
    }
}
package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        super(filmService);
        this.filmService = filmService;
        this.userService = userService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLikeToFilm(@PathVariable("userId") Integer userId, @PathVariable("id") Integer filmId) {
        trowIfUserNotExist(userId);
        trowIfFilmNotExist(filmId);
        filmService.addUserLikeToFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeUserLikeFromFilm(@PathVariable("userId") Integer userId, @PathVariable("id") Integer filmId) {
        trowIfUserNotExist(userId);
        trowIfFilmNotExist(filmId);
        filmService.removeUserLikeFromFilm(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsByLikes(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        return filmService.getFilmsByLikes(0, count);
    }

    @Override
    protected String getTitle() {
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
        if (userService.getById(id) == null) {
            log.debug("Не найден пользователь с таким ИД: {}", id);
            throw new NotFoundException();
        }
    }

    private void trowIfFilmNotExist(Integer id) {
        if (filmService.getById(id) == null) {
            log.debug("Не найден фильм с таким ИД: {}", id);
            throw new NotFoundException();
        }
    }
}

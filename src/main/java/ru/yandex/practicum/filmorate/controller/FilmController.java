package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        super(filmService);
        this.filmService = filmService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLikeToFilm(@PathVariable("userId") Integer userId, @PathVariable("id") Integer filmId) {
        log.debug("Пользователь с ИД {} добавил лайк к фильму с ИД {}",userId, filmId);
        filmService.addUserLikeToFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeUserLikeFromFilm(@PathVariable("userId") Integer userId, @PathVariable("id") Integer filmId) {
        log.debug("Пользователь с ИД {} убрал лайк к фильму с ИД {}",userId, filmId);
        filmService.removeUserLikeFromFilm(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsByLikes(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.debug("Возврат первых {} фильмов по количеству лайков", count);
        return filmService.getFilmsByLikes(0, count);
    }

}

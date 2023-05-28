package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private static final LocalDate BEFORE_MIN_RELEASE_DATE = LocalDate.of(1895, 12, 27);
    private FilmController controller;
    private Film defaultFilm;

    @BeforeEach
    void setUp() throws ValidationException {
        controller = new FilmController();
        defaultFilm = Film.builder()
                .description("Веселый фильм")
                .name("Ёлочка")
                .duration(3600)
                .releaseDate(LocalDate.of(1983, 12, 27)).build();
        defaultFilm = controller.create(defaultFilm);
    }

    @Test
    void findAllAndCreateAndUpdateForValidFilms() throws ValidationException, NotFoundException {
        Film firstFilm = Film.builder()
                .description("Хороший фильм")
                .name("Роман")
                .duration(3600)
                .releaseDate(LocalDate.of(1981, 12, 27)).build();
        Film secondFilm = Film.builder()
                .description("Хороший фильм, продолжение")
                .name("Роман 2")
                .duration(3600)
                .releaseDate(LocalDate.of(1982, 12, 27)).build();

        // Фильмы поностью валидны, добавляются и возвращаются
        firstFilm = controller.create(firstFilm);
        secondFilm = controller.create(secondFilm);
        assertEquals(3, controller.findAll().size());
        assertEquals(firstFilm.getName(), controller.findAll().get(1).getName());

        // Обновление валидного фильма
        secondFilm.setDescription("Хороший фильм, продолжение, чуть похуже первого");
        controller.update(secondFilm);
        assertEquals(3, controller.findAll().size());
        assertEquals("Хороший фильм, продолжение, чуть похуже первого", controller.findAll().get(2).getDescription());
    }

    @Test
    void shouldNotCreateNewFilmIfToLongDescription() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .description("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20" +
                                    "21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40" +
                                    "41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60" +
                                    "61 62 63 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80").build();
                    Film newFilm = controller.create(film);
                }
        );
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldNotCreateNewFilmIfEmptyName() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .name("").build();
                    Film newFilm = controller.create(film);
                }
        );
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateNewFilmIfReleaseDateWrong() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .releaseDate(BEFORE_MIN_RELEASE_DATE).build();
                    Film newFilm = controller.create(film);
                }
        );
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldNotCreateNewFilmIfDurationNegative() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .duration(0).build();
                    Film newFilm = controller.create(film);
                }
        );
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldNotUpdateFilmIfToLongDescription() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .description("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20" +
                                    "21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40" +
                                    "41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60" +
                                    "61 62 63 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80").build();
                    Film newFilm = controller.update(film);
                }
        );
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldNotUpdateFilmIfEmptyName() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .name("").build();
                    Film newFilm = controller.update(film);
                }
        );
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotUpdateFilmIfReleaseDateWrong() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .releaseDate(BEFORE_MIN_RELEASE_DATE).build();
                    Film newFilm = controller.update(film);
                }
        );
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldNotUpdateFilmIfDurationNegative() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .duration(0).build();
                    Film newFilm = controller.update(film);
                }
        );
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldNotUpdateIfIdNotExist() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .id(999).build();
                    Film newFilm = controller.update(film);
                }
        );
    }

    @Test
    void shouldNotUpdateIfIdIsNull() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .id(null).build();
                    Film newFilm = controller.update(film);
                }
        );
        assertEquals("ИД не может быть пустым", exception.getMessage());
    }
}
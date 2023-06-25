package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFriendsStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private static final LocalDate BEFORE_MIN_RELEASE_DATE = LocalDate.of(1895, 12, 27);
    private static final Integer NOT_EXIST_USER_ID = 100500;
    private static final Integer NOT_EXIST_FILM_ID = 100501;
    private FilmController controller;
    private UserController userController;
    private Film defaultFilm;
    private User defaultUser;

    @BeforeEach
    void setUp() throws ValidationException {
        UserService userService = new UserService(new InMemoryUserStorage(new InMemoryLikeStorage(), new InMemoryFriendsStorage()));
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(new InMemoryLikeStorage())),
                userService);
        defaultFilm = Film.builder()
                .description("Веселый фильм")
                .name("Ёлочка")
                .duration(3600)
                .releaseDate(LocalDate.of(1983, 12, 27)).build();
        defaultFilm = controller.create(defaultFilm);

        userController = new UserController(userService);
        defaultUser = User.builder()
                .name("Обычный пользователь")
                .email("user@mail.com")
                .login("defaultUser")
                .birthday(LocalDate.of(1995, 12, 28))
                .build();
        defaultUser = userController.create(defaultUser);
    }

    @Test
    void allValidOperations() throws ValidationException, NotFoundException {
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

        controller.addUserLikeToFilm(defaultUser.getId(), firstFilm.getId());
        List<Film> topFilms = controller.getFilmsByLikes(10);
        assertEquals(3, topFilms.size());
        assertEquals(firstFilm.getName(), topFilms.get(0).getName());

        controller.addUserLikeToFilm(defaultUser.getId(), secondFilm.getId());
        controller.removeUserLikeFromFilm(defaultUser.getId(), firstFilm.getId());
        topFilms = controller.getFilmsByLikes(10);
        assertEquals(3, topFilms.size());
        assertEquals(secondFilm.getName(), topFilms.get(0).getName());
    }

    @Test
    void should404ForNotExistUserWhenAddUserLikeToFilm() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.addUserLikeToFilm(NOT_EXIST_USER_ID, defaultFilm.getId());
                }
        );
    }

    @Test
    void should404ForNotExistFilmWhenAddUserLikeToFilm() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.addUserLikeToFilm(defaultUser.getId(), NOT_EXIST_FILM_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenRemoveUserLikeToFilm() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.removeUserLikeFromFilm(NOT_EXIST_USER_ID, defaultFilm.getId());
                }
        );
    }

    @Test
    void should404ForNotExistFilmWhenRemoveUserLikeToFilm() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.removeUserLikeFromFilm(defaultUser.getId(), NOT_EXIST_FILM_ID);
                }
        );
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
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    Film film = defaultFilm.toBuilder()
                            .id(null).build();
                    Film newFilm = controller.update(film);
                }
        );
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class GenreControllerIntegrationTest {
    private static final Integer NOT_EXIST_GENRE_ID = 100500;
    private final GenreController genreController;

    @Autowired
    public GenreControllerIntegrationTest(GenreController genreController) {
        this.genreController = genreController;
    }

    @Test
    void allValidOperations() throws ValidationException, NotFoundException {
        List<Genre> genres = genreController.findAllGenres();
        assertEquals(6, genres.size());
        Genre genre = genreController.getGenreById(1);
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void should404ForNotExistUserWhenGetFriendsByUserId() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    genreController.getGenreById(NOT_EXIST_GENRE_ID);
                }
        );
    }
}
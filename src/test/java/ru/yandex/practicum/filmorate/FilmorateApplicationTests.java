package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    private final FilmDbStorage filmDbStorage;

    @Test
    public void testFindUserById() {
        User defaultUser = User.builder()
                .name("Обычный пользователь")
                .email("user@mail.com")
                .login("defaultUser")
                .birthday(LocalDate.of(1995, 12, 28))
                .build();
        defaultUser = userStorage.create(defaultUser);
        defaultUser.setName("Обыватель");
        final User updatedUser = userStorage.update(defaultUser);

        Optional<User> userOptional = userStorage.getById(updatedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", updatedUser.getId())
                );
    }

    @Test
    public void testFindFilmById() {
        Film defaultFilm = Film.builder()
                .description("Веселый фильм")
                .mpa(Mpa.builder().id(1).build())
                .genres(new ArrayList<>())
                .name("Ёлочка")
                .duration(3600)
                .releaseDate(LocalDate.of(1983, 12, 27)).build();
        defaultFilm.getGenres().add(Genre.builder().id(1).build());
        defaultFilm = filmDbStorage.create(defaultFilm);
        defaultFilm.setName("Веселая братва");
        final Film updatedFilm = filmDbStorage.update(defaultFilm);

        Optional<Film> filmOptional = filmDbStorage.getById(updatedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", updatedFilm.getId())
                );
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    private UserController controller;
    private User defaultUser;

    @BeforeEach
    void setUp() throws ValidationException {
        controller = new UserController();
        defaultUser = User.builder()
                .name("Обычный пользователь")
                .email("user@mail.com")
                .login("defaultUser")
                .birthday(LocalDate.of(1995, 12, 28))
                .build();
        defaultUser = controller.create(defaultUser);
    }

    @Test
    void findAllAndCreateAndUpdateForValidUsers() throws ValidationException, NotFoundException {
        User firstUser = User.builder()
                .name("Новый пользователь")
                .email("newUser@mail.com")
                .login("newUser")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        User secondUser = User.builder()
                .name("Активный пользователь")
                .email("activeUser@mail.com")
                .login("activeUser")
                .birthday(LocalDate.of(1995, 12, 29))
                .build();

        // Фильмы поностью валидны, добавляются и возвращаются
        firstUser = controller.create(firstUser);
        secondUser = controller.create(secondUser);
        assertEquals(3, controller.findAll().size());
        assertEquals(firstUser.getName(), controller.findAll().get(1).getName());

        // Обновление валидного фильма
        secondUser.setName("Очень активный пользователь");
        controller.update(secondUser);
        assertEquals(3, controller.findAll().size());
        assertEquals("Очень активный пользователь", controller.findAll().get(2).getName());
    }

    @Test
    void shouldNotCreateNewUserIfEmailIsNotValid() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .email("newEmailIsWrong").build();
                    User newUser = controller.create(user);
                }
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateNewUserIfLoginIsNotValid() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .login("login with blank").build();
                    User newUser = controller.create(user);
                }
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotCreateNewUserIfBirthdayInTomorrow() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .birthday(LocalDate.now().plusDays(1)).build();
                    User newUser = controller.create(user);
                }
        );
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserIfEmailIsNotValid() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .email("newEmailIsWrong").build();
                    User newUser = controller.update(user);
                }
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserIfLoginIsNotValid() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .login("login with blank").build();
                    User newUser = controller.update(user);
                }
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserIfBirthdayInTomorrow() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .birthday(LocalDate.now().plusDays(1)).build();
                    User newUser = controller.update(user);
                }
        );
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldNotUpdateIfIdNotExist() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .id(999).build();
                    User newUser = controller.update(user);
                }
        );
    }

    @Test
    void shouldNotUpdateIfIdIsNull() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .id(null).build();
                    User newUser = controller.update(user);
                }
        );
        assertEquals("ИД не может быть пустым", exception.getMessage());
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerIntegrationTest {
    private static final Integer NOT_EXIST_USER_ID = 100500;
    private final UserController userController;
    private User defaultUser;

    @Autowired
    public UserControllerIntegrationTest(UserController userController) {
        this.userController = userController;
    }

    @BeforeEach
    void setUp() throws ValidationException {
        defaultUser = User.builder()
                .name("Обычный пользователь")
                .email("user@mail.com")
                .login("defaultUser")
                .birthday(LocalDate.of(1995, 12, 28))
                .build();
        defaultUser = userController.create(defaultUser);
    }

    @AfterEach
    void cleanDB() {
        userController.delete(defaultUser.getId());
    }

    @Test
    void allValidOperations() throws ValidationException, NotFoundException {
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

        // Пользователи поностью валидны, добавляются и возвращаются
        firstUser = userController.create(firstUser);
        secondUser = userController.create(secondUser);
        assertEquals(3, userController.findAll().size());
        assertEquals(firstUser.getName(), userController.findAll().get(1).getName());

        // Обновление валидного пользователя
        secondUser.setName("Очень активный пользователь");
        userController.update(secondUser);
        assertEquals(3, userController.findAll().size());
        assertEquals("Очень активный пользователь", userController.findAll().get(2).getName());

        userController.addFriendship(firstUser.getId(), secondUser.getId());
        List<User> firstUserFriends = userController.getFriendsByUserId(firstUser.getId());
        assertEquals(1, firstUserFriends.size());
        assertEquals(secondUser.getId(), firstUserFriends.get(0).getId());

        List<User> secondUserFriends = userController.getFriendsByUserId(secondUser.getId());
        assertEquals(0, secondUserFriends.size());

        userController.addFriendship(defaultUser.getId(), firstUser.getId());
        userController.addFriendship(defaultUser.getId(), secondUser.getId());

        List<User> firstUserCommonFriends = userController.getCommonFriends(firstUser.getId(), defaultUser.getId());
        assertEquals(1, firstUserCommonFriends.size());
        assertEquals(secondUser.getId(), firstUserCommonFriends.get(0).getId());

        userController.removeFriendship(firstUser.getId(), secondUser.getId());
        userController.removeFriendship(firstUser.getId(), defaultUser.getId());
        firstUserFriends = userController.getFriendsByUserId(firstUser.getId());
        assertEquals(0, firstUserFriends.size());
    }

    @Test
    void should404ForNotExistUserWhenGetFriendsByUserId() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.getFriendsByUserId(NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenGetCommonFriends() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.getCommonFriends(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenGetCommonFriends2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.getCommonFriends(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenAddFriendship() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.addFriendship(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenAddFriendship2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.addFriendship(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenRemoveFriendship() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.removeFriendship(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenRemoveFriendship2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    userController.removeFriendship(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void shouldNotCreateNewUserIfEmailIsNotValid() {
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .email("newEmailIsWrong").build();
                    User newUser = userController.create(user);
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
                    User newUser = userController.create(user);
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
                    User newUser = userController.create(user);
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
                    User newUser = userController.update(user);
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
                    User newUser = userController.update(user);
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
                    User newUser = userController.update(user);
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
                    User newUser = userController.update(user);
                }
        );
    }

    @Test
    void shouldNotUpdateIfIdIsNull() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .id(null).build();
                    User newUser = userController.update(user);
                }
        );
    }
}
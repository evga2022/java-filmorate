package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFriendsStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private static final Integer NOT_EXIST_USER_ID = 100500;
    private UserController controller;
    private User defaultUser;

    @BeforeEach
    void setUp() throws ValidationException {
        controller = new UserController(new UserService(new InMemoryUserStorage(new InMemoryLikeStorage(), new InMemoryFriendsStorage())));
        defaultUser = User.builder()
                .name("Обычный пользователь")
                .email("user@mail.com")
                .login("defaultUser")
                .birthday(LocalDate.of(1995, 12, 28))
                .build();
        defaultUser = controller.create(defaultUser);
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
        firstUser = controller.create(firstUser);
        secondUser = controller.create(secondUser);
        assertEquals(3, controller.findAll().size());
        assertEquals(firstUser.getName(), controller.findAll().get(1).getName());

        // Обновление валидного пользователя
        secondUser.setName("Очень активный пользователь");
        controller.update(secondUser);
        assertEquals(3, controller.findAll().size());
        assertEquals("Очень активный пользователь", controller.findAll().get(2).getName());

        controller.addFriendship(firstUser.getId(), secondUser.getId());
        List<User> firstUserFriends = controller.getFriendsByUserId(firstUser.getId());
        assertEquals(1, firstUserFriends.size());
        assertEquals(secondUser.getId(), firstUserFriends.get(0).getId());

        List<User> secondUserFriends = controller.getFriendsByUserId(secondUser.getId());
        assertEquals(1, secondUserFriends.size());
        assertEquals(firstUser.getId(), secondUserFriends.get(0).getId());

        controller.addFriendship(defaultUser.getId(), firstUser.getId());
        controller.addFriendship(defaultUser.getId(), secondUser.getId());

        List<User> firstUserCommonFriends = controller.getCommonFriends(firstUser.getId(), secondUser.getId());
        assertEquals(1, firstUserCommonFriends.size());
        assertEquals(defaultUser.getId(), firstUserCommonFriends.get(0).getId());

        controller.removeFriendship(firstUser.getId(), secondUser.getId());
        controller.removeFriendship(firstUser.getId(), defaultUser.getId());
        firstUserFriends = controller.getFriendsByUserId(firstUser.getId());
        assertEquals(0, firstUserFriends.size());
    }

    @Test
    void should404ForNotExistUserWhenGetFriendsByUserId() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.getFriendsByUserId(NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenGetCommonFriends() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.getCommonFriends(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenGetCommonFriends2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.getCommonFriends(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenAddFriendship() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.addFriendship(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenAddFriendship2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.addFriendship(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenRemoveFriendship() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.removeFriendship(NOT_EXIST_USER_ID, defaultUser.getId());
                }
        );
    }

    @Test
    void should404ForNotExistUserWhenRemoveFriendship2() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    controller.removeFriendship(defaultUser.getId(), NOT_EXIST_USER_ID);
                }
        );
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
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    User user = defaultUser.toBuilder()
                            .id(null).build();
                    User newUser = controller.update(user);
                }
        );
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class MpaControllerIntegrationTest {
    private static final Integer NOT_EXIST_MPA_ID = 100500;
    private final MpaController mpaController;

    @Autowired
    public MpaControllerIntegrationTest(MpaController mpaController) {
        this.mpaController = mpaController;
    }

    @Test
    void allValidOperations() throws ValidationException, NotFoundException {
        List<Mpa> mpas = mpaController.findAllMpa();
        assertEquals(5, mpas.size());
        Mpa mpa = mpaController.getMpaById(1);
        assertEquals("G", mpa.getName());
    }

    @Test
    void should404ForNotExistUserWhenGetFriendsByUserId() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> {
                    mpaController.getMpaById(NOT_EXIST_MPA_ID);
                }
        );
    }
}
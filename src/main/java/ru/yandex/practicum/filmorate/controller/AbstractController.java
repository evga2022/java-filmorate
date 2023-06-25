package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.HasId;
import ru.yandex.practicum.filmorate.service.AbstractService;

import java.util.List;

@Slf4j
public abstract class AbstractController<T extends HasId> {
    private final AbstractService<T> abstractService;

    protected AbstractController(AbstractService<T> abstractService) {
        this.abstractService = abstractService;
    }

    @GetMapping
    public List<T> findAll() {
        return abstractService.findAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable("id") Integer id) {
        T result = abstractService.getById(id);
        if (result == null) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), id);
            throw new NotFoundException();
        }
        return result;
    }

    @PostMapping
    public T create(@RequestBody T newObject) {
        validate(newObject);
        log.debug("Новый {}: {}", getTitle(), newObject);
        return abstractService.create(newObject);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        abstractService.delete(id);
    }

    @PutMapping
    public T update(@RequestBody T updatedObject) {
        validate(updatedObject);
        if (abstractService.getById(updatedObject.getId()) == null) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), updatedObject.getId());
            throw new NotFoundException();
        }
        log.debug("Обновлен {}: {}", getTitle(), updatedObject);
        return abstractService.update(updatedObject);
    }

    protected abstract String getTitle();

    protected void validate(T validatedObject) {
        ValidationException validationException = doValidate(validatedObject);
        if (validationException != null) {
            log.debug("Непройдена валидация для: {}, сгенерировано исключение: {}", validatedObject, validationException);
            throw validationException;
        }
    }

    /**
     * Функция должна вернуть null если объект валидный, и ValidationException для невалидных объектов
     *
     * @param validatedObject - объект для валидации
     * @return возвращает null для валидных объектов
     */
    protected ValidationException doValidate(T validatedObject) {
        return null;
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.HasId;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;
import java.util.Optional;


@Slf4j
public abstract class AbstractService<T extends HasId> {

    private final AbstractStorage<T> storage;

    AbstractService(AbstractStorage<T> storage) {
        this.storage = storage;
    }

    public T create(T newObject) {
        validate(newObject);
        log.debug("Новый {}: {}", getTitle(), newObject);
        return storage.create(newObject);
    }

    public T update(T updatedObject) {
        validate(updatedObject);
        if (storage.getById(updatedObject.getId()) == null) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), updatedObject.getId());
            throw new NotFoundException();
        }
        log.debug("Обновлен {}: {}", getTitle(), updatedObject);
        return storage.update(updatedObject);
    }

    public T getById(Integer id) {
        Optional<T> result = storage.getById(id);
        if (result.isEmpty()) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), id);
            throw new NotFoundException();
        }
        return result.get();
    }

    public void delete(Integer id) {
        log.debug("Удален {} с ИД: {}", getTitle(), id);
        storage.delete(id);
    }

    public List<T> findAll() {
        return storage.findAll();
    }

    public abstract String getTitle();

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

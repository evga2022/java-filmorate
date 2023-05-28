package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.HasId;

import java.util.HashMap;
import java.util.List;

@Slf4j
public abstract class AbstractController<T extends HasId> {
    private final HashMap<Integer, T> objectStore = new HashMap<>();
    private int currentNextId = 1;

    @GetMapping
    public List<T> findAll() {
        return List.copyOf(objectStore.values());
    }

    @PostMapping
    public T create(@RequestBody T newObject) throws ValidationException {
        if (newObject.getId() == null) {
            newObject.setId(currentNextId);
        }
        validate(newObject);
        log.debug("Новый {}: {}", getTitle(), newObject);
        objectStore.put(newObject.getId(), newObject);
        currentNextId++;
        return newObject;
    }

    @PutMapping
    public T update(@RequestBody T updatedObject) throws ValidationException, NotFoundException {
        validate(updatedObject);
        if (!objectStore.containsKey(updatedObject.getId())) {
            log.debug("Не найден {} с таким ИД: {}", getTitle(), updatedObject.getId());
            throw new NotFoundException();
        }
        log.debug("Обновлен {}: {}", getTitle(), updatedObject);
        objectStore.put(updatedObject.getId(), updatedObject);
        return updatedObject;
    }

    protected abstract String getTitle();

    protected void validate(T validatedObject) throws ValidationException {
        ValidationException validationException = doValidate(validatedObject);
        if (validationException != null) {
            log.debug("Непройдена валидация для: {}, сгенерировано исключение: {}", validatedObject, validationException);
            throw validationException;
        }
    }

    ;

    /**
     * Функция должна вернуть null если объект валидный, и ValidationException для невалидных объектов
     *
     * @param validatedObject - объект для валидации
     * @return возвращает null для валидных объектов
     */
    protected ValidationException doValidate(T validatedObject) {
        if (validatedObject.getId() == null) {
            return new ValidationException("ИД не может быть пустым");
        }
        return null;
    }
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.HasId;

import java.util.List;
import java.util.Optional;

public interface AbstractStorage<T extends HasId> {
    T create(T newObject);

    T update(T updatedObject);

    Optional<T> getById(Integer id);

    void delete(Integer id);

    List<T> findAll();
}

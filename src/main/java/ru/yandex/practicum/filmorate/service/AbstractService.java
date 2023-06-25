package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.HasId;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

public class AbstractService<T extends HasId> {

    private final AbstractStorage<T> storage;

    AbstractService(AbstractStorage<T> storage) {
        this.storage = storage;
    }

    public T create(T newObject) {
        return storage.create(newObject);
    }

    public T update(T updatedObject) {
        return storage.update(updatedObject);
    }

    public T getById(Integer id) {
        return storage.getById(id);
    }

    public void delete(Integer id) {
        storage.delete(id);
    }

    public List<T> findAll() {
        return storage.findAll();
    }
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.HasId;

import java.util.HashMap;
import java.util.List;

public class InMemoryAbstractStorage<T extends HasId> implements AbstractStorage<T> {

    private final HashMap<Integer, T> objectStore = new HashMap<>();
    private int currentNextId = 1;

    @Override
    public T create(T newObject) {
        newObject.setId(currentNextId);
        objectStore.put(newObject.getId(), newObject);
        currentNextId++;
        return newObject;
    }

    @Override
    public T update(T updatedObject) {
        objectStore.put(updatedObject.getId(), updatedObject);
        return updatedObject;
    }

    @Override
    public T getById(Integer id) {
        return objectStore.get(id);
    }

    @Override
    public void delete(Integer id) {
        objectStore.remove(id);
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(objectStore.values());
    }
}

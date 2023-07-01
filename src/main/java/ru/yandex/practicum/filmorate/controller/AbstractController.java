package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.debug("Получение всех {}", abstractService.getTitle());
        return abstractService.findAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable("id") Integer id) {
        log.debug("Получение {} с ИД: {}", abstractService.getTitle(), id);
        return abstractService.getById(id);
    }

    @PostMapping
    public T create(@RequestBody T newObject) {
        log.debug("Новый {}: {}", abstractService.getTitle(), newObject);
        return abstractService.create(newObject);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.debug("Удаление {} с ИД: {}", abstractService.getTitle(), id);
        abstractService.delete(id);
    }

    @PutMapping
    public T update(@RequestBody T updatedObject) {
        log.debug("Обновлен {}: {}", abstractService.getTitle(), updatedObject);
        return abstractService.update(updatedObject);
    }

}

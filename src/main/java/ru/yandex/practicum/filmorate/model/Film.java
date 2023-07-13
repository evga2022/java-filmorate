package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Film implements HasId {
    private Integer id;
    private String name;
    private String description;
    private Mpa mpa;
    private List<Genre> genres;
    private LocalDate releaseDate;
    private long duration;
}

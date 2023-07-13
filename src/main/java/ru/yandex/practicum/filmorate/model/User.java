package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User implements HasId {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    /**
     * Признак того является ли этот пользователь подтвержденным другом
     */
    private Boolean isFriend;
}

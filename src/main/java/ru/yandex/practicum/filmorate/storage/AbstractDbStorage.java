package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.HasId;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDbStorage<T extends HasId> implements AbstractStorage<T> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> mapper;

    public AbstractDbStorage(JdbcTemplate jdbcTemplate, RowMapper<T> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public abstract T create(T newObject);

    @Override
    public abstract T update(T updatedObject);

    @Override
    public Optional<T> getById(Integer id) {
        String sql = "SELECT * FROM " + getResourceName() +
                " WHERE " + getResourceIdName() + " = ?";
        List<T> result = jdbcTemplate.query(sql, mapper, id);
        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM " + getResourceName() + " WHERE " + getResourceIdName() + " = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getResourceName();
        return jdbcTemplate.query(sql, mapper);
    }

    protected abstract String getResourceIdName();

    protected abstract String getResourceName();
}

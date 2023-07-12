package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage extends AbstractDbStorage<Film> implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> mapper;

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Mpa> mpaMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new FilmMapper());
        this.mapper = new FilmMapper();
        this.genreMapper = new GenreMapper();
        this.mpaMapper = new MpaMapper();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film newObject) {
        String sql = "INSERT INTO " + getResourceName() + "(name, description, release_date, mpa_id, duration) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING " + getResourceIdName();
        List<Integer> insertedIds = jdbcTemplate.query(sql,
                Arrays.asList(newObject.getName(),
                        newObject.getDescription(), newObject.getReleaseDate(),
                        newObject.getMpa().getId(), newObject.getDuration()).toArray(), new int[0],
                new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt(getResourceIdName());
                    }
                });
        addAllFilmGenres(newObject.getGenres(), insertedIds.get(0));
        return getById(insertedIds.get(0)).orElseThrow();
    }

    @Override
    public Optional<Film> getById(Integer id) {
        String sql = "SELECT f.*, m.mpa_id, m.name AS mpa_name FROM "
                + getResourceName() + " AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id AND "
                + getResourceIdName() + " = ? ";
        List<Film> result = jdbcTemplate.query(sql, mapper, id);
        if (!result.isEmpty()) {
            return Optional.of(populateFilm(result.get(0)));
        } else {
            return Optional.empty();
        }
    }

    private Film populateFilm(Film film) {
        film.setGenres(findGenresByFilmId(film.getId()));
        return film;
    }

    private List<Genre> findGenresByFilmId(Integer id) {
        String sql = "SELECT * FROM genre WHERE film_id = ?";
        return jdbcTemplate.query(sql, genreMapper, id);
    }

    @Override
    public Film update(Film updatedObject) {
        String sql = "UPDATE " + getResourceName() +
                "SET name = ?, description = ?, release_date = ?, mpa_id = ?, duration = ? " +
                "WHERE " + getResourceIdName() + " = ?";
        jdbcTemplate.update(sql, updatedObject.getName(),
                updatedObject.getDescription(), updatedObject.getReleaseDate(),
                updatedObject.getMpa().getId(), updatedObject.getDuration(), updatedObject.getId());
        deleteFilmGenresById(updatedObject.getId());
        addAllFilmGenres(updatedObject.getGenres(), updatedObject.getId());
        return getById(updatedObject.getId()).orElseThrow();
    }

    private void addAllFilmGenres(List<Genre> genres, Integer filmId) {
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> {
                String sql = "INSERT INTO film_genres(film_id, genre_id) " +
                        "VALUES(?, ?) ";
                jdbcTemplate.update(sql, filmId, genre.getId());
            });
        }
    }

    @Override
    public void delete(Integer id) {
        deleteFilmGenresById(id);
        deleteFavoriteFilmsById(id);
        super.delete(id);
    }

    private void deleteFilmGenresById(Integer id) {
        String filmGenresSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(filmGenresSql, id);
    }

    private void deleteFavoriteFilmsById(Integer id) {
        String favoriteFilmsSql = "DELETE FROM favorite_films WHERE film_id = ?";
        jdbcTemplate.update(favoriteFilmsSql, id);
    }

    @Override
    protected String getResourceIdName() {
        return "film_id";
    }

    @Override
    protected String getResourceName() {
        return "film";
    }

    @Override
    public void addUserLikeToFilm(Integer userId, Integer filmId) {
        String sql = "INSERT INTO favorite_films(film_id, user_id) " +
                "VALUES(?, ?) ";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeUserLikeFromFilm(Integer userId, Integer filmId) {
        String sql = "DELETE FROM favorite_films WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getFilmsByLikes(Integer from, Integer limit) {
        String sql = "SELECT f.*, m.mpa_id, m.name AS mpa_name, COUNT(ff.favorite_films_id) AS likes FROM film AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN favorite_films AS ff ON f.film_id = ff.film_id " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY likes DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, mapper, limit, from).stream().map(this::populateFilm).collect(Collectors.toList());
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.mpa_id, m.name AS mpa_name FROM film AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id ";
        return jdbcTemplate.query(sql, mapper).stream().map(this::populateFilm).collect(Collectors.toList());
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ";
        return jdbcTemplate.query(sql, genreMapper);
    }

    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        List<Genre> result = jdbcTemplate.query(sql, genreMapper, id);
        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM genre ";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    public Optional<Mpa> getMpaById(Integer id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        List<Mpa> result = jdbcTemplate.query(sql, mpaMapper, id);
        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }
}

package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage extends AbstractDbStorage<User> implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new UserMapper());
        this.mapper = new UserMapper();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User newObject) {
        String sql = "INSERT INTO " + getResourceName() + "(name, email, login, birth_date) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING " + getResourceIdName();
        List<Integer> insertedIds = jdbcTemplate.query(sql,
                Arrays.asList(newObject.getName(),
                        newObject.getEmail(), newObject.getLogin(),
                        newObject.getBirthday()).toArray(), new int[0],
                new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt(getResourceIdName());
                    }
                });
        return super.getById(insertedIds.get(0)).orElseThrow();
    }

    @Override
    public User update(User updatedObject) {
        String sql = "UPDATE " + getResourceName() +
                "SET email = ?, name = ?, login = ?, birth_date = ? " +
                "WHERE " + getResourceIdName() + " = ?";
        jdbcTemplate.update(sql, updatedObject.getEmail(),
                updatedObject.getName(), updatedObject.getLogin(),
                updatedObject.getBirthday(), updatedObject.getId());
        return super.getById(updatedObject.getId()).orElseThrow();
    }

    @Override
    public void delete(Integer id) {
        String favoriteFilmsSql = "DELETE FROM favorite_films WHERE user_id = ?";
        jdbcTemplate.update(favoriteFilmsSql, id);
        String friendshipsSql = "DELETE FROM friendships WHERE user_left_id = ? OR user_right_id = ?";
        jdbcTemplate.update(friendshipsSql, id, id);
        super.delete(id);
    }

    @Override
    protected String getResourceIdName() {
        return "user_id";
    }

    @Override
    protected String getResourceName() {
        return "film_user";
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        String sql = "SELECT fu.*, COUNT(fsr.friendships_id) > 0 AS is_friend FROM film_user AS fu " +
                "JOIN friendships AS fs ON fs.user_right_id = ? AND fu.user_id = fs.user_left_id " +
                "LEFT JOIN friendships AS fsr ON fsr.user_right_id = fs.user_left_id AND fsr.user_left_id = fs.user_right_id " +
                "GROUP BY fu.user_id " +
                "ORDER BY fu.name ";
        return jdbcTemplate.query(sql, mapper, id);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        String sql = "SELECT fu.*, COUNT(fsr.friendships_id) > 0 AS is_friend FROM film_user AS fu " +
                "JOIN friendships AS fs ON fs.user_right_id = ? AND fu.user_id = fs.user_left_id " +
                "LEFT JOIN friendships AS fsr ON fsr.user_right_id = fs.user_left_id AND fsr.user_left_id = fs.user_right_id " +
                "JOIN friendships AS fso ON fso.user_right_id = ? AND fu.user_id = fso.user_left_id " +
                "GROUP BY fu.user_id " +
                "ORDER BY fu.name ";
        return jdbcTemplate.query(sql, mapper, userId, otherId);
    }

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friendships(user_left_id, user_right_id) " +
                "VALUES(?, ?) ";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendships WHERE user_left_id = ? AND user_right_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }
}

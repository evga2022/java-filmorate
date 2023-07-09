# java-filmorate

### Схема бызы данных для Filmorate project.
![Схема базы данных.](./database-schema.png)

### Примеры запросов для основных операций приложения

1. Пример запроса для получения списка всех фильмов
```
SELECT * 
FROM film
```

1. Пример запроса для получения топ N наиболее популярных фильмов
```
SELECT 
    f.*,
    COUNT(ff.favorite_films_id) AS likes_count
FROM film AS f
LEFT JOIN favorite_films AS ff ON f.film_id=ff.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT 10
```

1. Пример запроса для получения списка друзей пользователя(только подтвержденных) где {ID} это идентификатор пользователя
```
SELECT  
    u.*
FROM frendships AS f
JOIN user AS u ON f.accepted=true 
    AND (f.user_left_id = {ID} OR f.user_right_id = {ID}) 
    AND (f.user_left_id = u.user_id OR f.user_right_id = u.user_id)
    AND u.user_id <> {ID}
```

1. Пример запроса для получения списка друзей пользователя(ожидающих подтверждения) где {ID} это идентификатор пользователя
```
SELECT  
    u.*
FROM frendships AS f
JOIN user AS u ON f.accepted=false 
    AND f.user_right_id = {ID} 
    AND f.user_left_id = u.user_id
```
1. Пример запроса для получения списка общих друзей пользователей(только подтвержденных) где {ID_1} {ID_2} это идентификаторы пользователей
```
SELECT  
    u.*
FROM user AS u
JOIN frendships AS f1 ON f1.accepted=true 
    AND (f1.user_left_id = {ID_1} OR f1.user_right_id = {ID_1}) 
    AND (f1.user_left_id = u.user_id OR f1.user_right_id = u.user_id)
    AND u.user_id <> {ID_1}
JOIN frendships AS f2 ON f2.accepted=true 
    AND (f2.user_left_id = {ID_2} OR f2.user_right_id = {ID_2}) 
    AND (f2.user_left_id = u.user_id OR f2.user_right_id = u.user_id)
    AND u.user_id <> {ID_2}
```
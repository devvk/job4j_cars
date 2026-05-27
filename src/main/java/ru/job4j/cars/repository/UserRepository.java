package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.command.CrudRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final CrudRepository crudRepository;

    public Optional<User> save(User user) {
        try {
            crudRepository.run(session -> session.persist(user));
            return Optional.of(user);
        } catch (HibernateException e) {
            if (isUniqueConstraintViolation(e)) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private static boolean isUniqueConstraintViolation(Throwable exception) {
        while (exception != null) {
            if (exception instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())) {
                return true;
            }
            exception = exception.getCause();
        }
        return false;
    }

    /**
     * Обновить в базе пользователя.
     *
     * @param user пользователь.
     */
    public void update(User user) {
        crudRepository.run(session -> session.merge(user));
    }

    /**
     * Удалить пользователя по id.
     *
     * @param userId ID
     */
    public void delete(Integer userId) {
        crudRepository.run(
                "DELETE FROM User WHERE id = :id",
                Map.of("id", userId)
        );
    }

    /**
     * Список пользователь отсортированных по id.
     *
     * @return список пользователей.
     */
    public List<User> findAllOrderById() {
        return crudRepository.query("FROM User ORDER BY id", User.class);
    }

    /**
     * Найти пользователя по ID
     *
     * @return пользователь.
     */
    public Optional<User> findById(Integer userId) {
        return crudRepository.optional(
                "FROM User WHERE id = :id", User.class,
                Map.of("id", userId));
    }

    /**
     * Список пользователей по login LIKE %key%
     *
     * @param key key
     * @return список пользователей.
     */
    public List<User> findByLikeLogin(String key) {
        return crudRepository.query(
                "FROM User WHERE login LIKE :key", User.class,
                Map.of("key", "%" + key + "%"));
    }

    /**
     * Найти пользователя по login.
     *
     * @param login login.
     * @return Optional or user.
     */
    public Optional<User> findByLogin(String login) {
        return crudRepository.optional(
                "FROM User WHERE login = :login", User.class,
                Map.of("login", login));
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "FROM User WHERE login = :login AND password = :password", User.class,
                Map.of("login", login,
                        "password", password)
        );
    }
}

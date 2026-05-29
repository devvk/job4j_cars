package ru.job4j.cars.repository.user;

import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> save(User user);

    void update(User user);

    void delete(Integer id);

    List<User> findAllOrderById();

    Optional<User> findById(Integer id);

    List<User> findByLikeLogin(String key);

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);
}

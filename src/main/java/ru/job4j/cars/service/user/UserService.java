package ru.job4j.cars.service.user;

import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> save(User user);

    void update(User user);

    void delete(Integer userId);

    List<User> findAllOrderedById();

    Optional<User> findById(Integer userId);

    List<User> findByLikeLogin(String key);

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);
}

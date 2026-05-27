package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> save(User user) {
        return userRepository.save(user);
    }

    public void update(User user) {
        userRepository.update(user);
    }

    public void delete(Integer userId) {
        userRepository.delete(userId);
    }

    public List<User> findAllOrderedById() {
        return userRepository.findAllOrderById();
    }

    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public List<User> findByLikeLogin(String key) {
        return userRepository.findByLikeLogin(key);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password);
    }
}

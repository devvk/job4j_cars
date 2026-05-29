package ru.job4j.cars.repository.engine;

import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {

    Engine create(Engine engine);

    void update(Engine engine);

    void delete(Integer id);

    List<Engine> findAllOrderById();

    Optional<Engine> findById(Integer id);
}

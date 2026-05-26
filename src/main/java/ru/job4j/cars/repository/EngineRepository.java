package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class EngineRepository {

    private final CrudRepository crudRepository;

    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    public void update(Engine engine) {
        crudRepository.run(session -> session.merge(engine));
    }

    public void delete(Integer engineId) {
        crudRepository.run(
                """
                        DELETE FROM Engine
                        WHERE id = :id
                        """,
                Map.of("id", engineId)
        );
    }

    public List<Engine> findAllOrderById() {
        return crudRepository.query(
                """
                        FROM Engine
                        ORDER BY id ASC
                        """,
                Engine.class
        );
    }

    public Optional<Engine> findById(Integer engineId) {
        return crudRepository.optional(
                """
                        FROM Engine
                        WHERE id = :id
                        """,
                Engine.class,
                Map.of("id", engineId)
        );
    }
}

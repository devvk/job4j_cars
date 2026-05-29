package ru.job4j.cars.repository.engine;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernateEngineRepository implements EngineRepository {

    private final CrudRepository crudRepository;

    @Override
    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    @Override
    public void update(Engine engine) {
        crudRepository.run(session -> session.merge(engine));
    }

    @Override
    public void delete(Integer engineId) {
        crudRepository.run(
                """
                        DELETE FROM Engine
                        WHERE id = :id
                        """,
                Map.of("id", engineId)
        );
    }

    @Override
    public List<Engine> findAllOrderById() {
        return crudRepository.query(
                """
                        FROM Engine
                        ORDER BY id ASC
                        """,
                Engine.class
        );
    }

    @Override
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

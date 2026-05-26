package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CarRepository {

    private final CrudRepository crudRepository;

    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    public void update(Car car) {
        crudRepository.run(session -> session.merge(car));
    }

    public void delete(Integer carId) {
        crudRepository.run(
                """
                        DELETE FROM Car c
                        WHERE c.id = :id
                        """,
                Map.of("id", carId)
        );
    }

    public List<Car> findAllOrderById() {
        return crudRepository.query(
                """
                        FROM Car c
                        ORDER BY c.id ASC
                        """,
                Car.class
        );
    }

    public Optional<Car> findById(Integer carId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT c
                        FROM Car c
                        LEFT JOIN FETCH c.engine
                        LEFT JOIN FETCH c.owners
                        WHERE c.id = :id
                        """,
                Car.class,
                Map.of("id", carId)
        );
    }

    public List<Car> findByEngineId(Integer engineId) {
        return crudRepository.query(
                """
                        SELECT DISTINCT c
                        FROM Car c
                        LEFT JOIN FETCH c.engine
                        LEFT JOIN FETCH c.owners
                        WHERE c.engine.id = :engineId
                        ORDER BY c.id ASC
                        """,
                Car.class,
                Map.of("engineId", engineId)
        );
    }
}

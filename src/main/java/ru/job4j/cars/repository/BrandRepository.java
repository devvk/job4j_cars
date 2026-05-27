package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BrandRepository {

    private final CrudRepository crudRepository;

    public List<Brand> findAllOrderByName() {
        return crudRepository.query(
                """
                        FROM Brand b
                        ORDER BY b.name ASC
                        """,
                Brand.class
        );
    }

    public Optional<Brand> findById(Integer id) {
        return crudRepository.optional(
                """
                        FROM Brand b
                        WHERE b.id = :id
                        """,
                Brand.class,
                Map.of("id", id)
        );
    }
}

package ru.job4j.cars.repository.photo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernatePhotoRepository implements PhotoRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Photo> findById(int id) {
        return crudRepository.optional(
                """
                        FROM Photo p
                        WHERE p.id = :id
                        """,
                Photo.class,
                Map.of("id", id)
        );
    }
}

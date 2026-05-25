package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class PostRepository {

    private final CrudRepository crudRepository;

    public List<Post> findAllCreatedLastDay() {
        return crudRepository.query(
                """
                        FROM Post
                        WHERE created >= :date
                        """,
                Post.class,
                Map.of("date", LocalDateTime.now().minusDays(1))
        );
    }

    // TODO Post.photo???
    public List<Post> findAllWithPhoto() {
        return crudRepository.query(
                """
                        FROM Post
                        WHERE photo IS NOT NULL
                        """,
                Post.class
        );
    }

    public List<Post> findAllByName(String brand) {
        return crudRepository.query(
                """
                        FROM Post
                        WHERE car.name = :brand
                        """,
                Post.class,
                Map.of("brand", brand)
        );
    }
}

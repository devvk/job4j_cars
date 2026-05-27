package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.command.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepository {

    private final CrudRepository crudRepository;

    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    public void delete(Integer postId) {
        crudRepository.run(session -> {
            var post = session.find(Post.class, postId);
            if (post != null) {
                session.remove(post);
            }
        });
    }

    public List<Post> findAllOrderedById() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p
                        FROM Post p
                        JOIN FETCH p.user
                        JOIN FETCH p.car c
                        LEFT JOIN FETCH c.brand
                        JOIN FETCH c.engine
                        LEFT JOIN FETCH p.photos
                        ORDER BY p.id DESC
                        """,
                Post.class
        );
    }

    public Optional<Post> findById(Integer postId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT p
                        FROM Post p
                        JOIN FETCH p.user
                        JOIN FETCH p.car c
                        LEFT JOIN FETCH c.brand
                        JOIN FETCH c.engine
                        LEFT JOIN FETCH p.photos
                        WHERE p.id = :id
                        """,
                Post.class,
                Map.of("id", postId)
        );
    }

    public List<Post> findAllCreatedLastDay() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p
                        FROM Post p
                        JOIN FETCH p.user
                        JOIN FETCH p.car c
                        LEFT JOIN FETCH c.brand
                        JOIN FETCH c.engine
                        LEFT JOIN FETCH p.photos
                        WHERE created >= :date
                        ORDER BY p.created DESC
                        """,
                Post.class,
                Map.of("date", LocalDateTime.now().minusDays(1))
        );
    }

    public List<Post> findAllWithPhoto() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p
                        FROM Post p
                        JOIN FETCH p.user
                        JOIN FETCH p.car c
                        LEFT JOIN FETCH c.brand
                        JOIN FETCH c.engine
                        JOIN FETCH p.photos
                        ORDER BY p.created DESC
                        """,
                Post.class
        );
    }

    public List<Post> findAllByCarModel(String model) {
        return crudRepository.query(
                """
                        SELECT DISTINCT p
                        FROM Post p
                        JOIN FETCH p.user
                        JOIN FETCH p.car c
                        LEFT JOIN FETCH c.brand
                        JOIN FETCH c.engine
                        LEFT JOIN FETCH p.photos
                        WHERE c.model = :model
                        ORDER BY p.created DESC
                        """,
                Post.class,
                Map.of("model", model)
        );
    }
}

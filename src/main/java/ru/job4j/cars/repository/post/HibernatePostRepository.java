package ru.job4j.cars.repository.post;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.command.CrudRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernatePostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    @Override
    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    @Override
    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    @Override
    public void delete(Integer postId) {
        crudRepository.run(session -> {
            var post = session.find(Post.class, postId);
            if (post != null) {
                session.remove(post);
            }
        });
    }

    @Override
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

    @Override
    public List<Post> findByFilter(PostFilter filter) {
        return crudRepository.query(session -> {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> post = criteriaQuery.from(Post.class);

            post.fetch("user", JoinType.INNER);
            Fetch<Object, Object> car = post.fetch("car", JoinType.INNER);
            car.fetch("brand", JoinType.LEFT);
            car.fetch("engine", JoinType.INNER);

            if (filter == PostFilter.WITH_PHOTO) {
                post.fetch("photos", JoinType.INNER);
            } else {
                post.fetch("photos", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter == PostFilter.TODAY) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        post.get("created"),
                        LocalDateTime.now().minusDays(1)
                ));
            }

            criteriaQuery.select(post).distinct(true);
            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            if (filter == PostFilter.TODAY) {
                criteriaQuery.orderBy(criteriaBuilder.desc(post.get("created")));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(post.get("id")));
            }

            return session.createQuery(criteriaQuery).getResultList();
        });
    }
}

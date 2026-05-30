package ru.job4j.cars.repository.post;

import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post create(Post post);

    void update(Post post);

    void delete(Integer id);

    Optional<Post> findById(Integer id);

    List<Post> findByFilter(PostFilter filter);
}

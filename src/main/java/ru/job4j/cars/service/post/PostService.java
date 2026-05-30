package ru.job4j.cars.service.post;

import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> findByFilter(PostFilter filter);

    Post create(Post post, User user, MultipartFile file);

    Optional<Post> update(int postId,
                          Post post,
                          Integer brandId,
                          Integer engineId,
                          MultipartFile file,
                          User user);

    boolean deleteById(int postId, User user);

    Optional<Post> findById(int id);

    boolean markAsSold(int postId, User user);
}

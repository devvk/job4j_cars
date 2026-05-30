package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.car.HibernateCarRepository;
import ru.job4j.cars.repository.post.HibernatePostRepository;
import ru.job4j.cars.service.file.SimpleFileService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SimplePostService implements PostService {

    private final HibernatePostRepository postRepository;
    private final HibernateCarRepository carRepository;
    private final SimpleFileService fileService;

    @Override
    public Post create(Post post, User user, MultipartFile file) {
        post.setCreated(LocalDateTime.now());
        post.setUser(user);

        Car savedCar = carRepository.create(post.getCar());
        post.setCar(savedCar);

        addPhoto(file, post);
        return postRepository.create(post);
    }

    @Override
    public Optional<Post> update(int postId,
                                 Post updatedPost,
                                 Integer brandId,
                                 Integer engineId,
                                 MultipartFile file,
                                 User user) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.warn("Post update failed. Post not found. postId={}, userId={}", postId, user.getId());
            return Optional.empty();
        }

        Post post = postOptional.get();
        if (!isOwner(post, user)) {
            log.warn("Post update failed. Access denied. postId={}, ownerId={}, userId={}",
                    postId, post.getUser().getId(), user.getId());
            return Optional.empty();
        }

        Brand brand = new Brand();
        brand.setId(brandId);
        Engine engine = new Engine();
        engine.setId(engineId);
        Car car = post.getCar();
        car.setBrand(brand);
        car.setEngine(engine);
        car.setModel(updatedPost.getCar().getModel());
        car.setBodyType(updatedPost.getCar().getBodyType());

        post.setDescription(updatedPost.getDescription());
        post.setSold(updatedPost.isSold());

        addPhoto(file, post);

        carRepository.update(car);
        postRepository.update(post);
        return Optional.of(post);
    }

    private void addPhoto(MultipartFile file, Post post) {
        if (file.isEmpty()) {
            return;
        }

        try {
            String path = fileService.save(new FileDto(file.getOriginalFilename(), file.getBytes()));

            Photo photo = new Photo();
            photo.setName(file.getOriginalFilename());
            photo.setPath(path);
            photo.setPost(post);

            post.getPhotos().add(photo);
        } catch (IOException e) {
            log.error("Error while uploading file", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(int postId, User user) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.warn("Post delete failed. Post not found. postId={}, userId={}",
                    postId, user.getId());
            return false;
        }

        Post post = postOptional.get();
        if (!isOwner(post, user)) {
            log.warn("Post delete failed. Access denied. postId={}, ownerId={}, userId={}",
                    postId, post.getUser().getId(), user.getId());
            return false;
        }

        Integer carId = post.getCar().getId();
        post.getPhotos().forEach(photo -> fileService.deleteByPath(photo.getPath()));

        postRepository.delete(postId);
        carRepository.delete(carId);
        return true;
    }

    private boolean isOwner(Post post, User user) {
        return post.getUser().getId().equals(user.getId());
    }

    @Override
    public List<Post> findByFilter(PostFilter filter) {
        return postRepository.findByFilter(filter);
    }

    @Override
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public boolean markAsSold(int postId, User user) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.warn("Post sold status update failed. Post not found. postId={}, userId={}",
                    postId, user.getId());
            return false;
        }

        Post post = postOptional.get();
        if (!isOwner(post, user)) {
            log.warn("Post sold status update failed. Access denied. postId={}, ownerId={}, userId={}",
                    postId, post.getUser().getId(), user.getId());
            return false;
        }

        post.setSold(true);
        postRepository.update(post);
        return true;
    }
}

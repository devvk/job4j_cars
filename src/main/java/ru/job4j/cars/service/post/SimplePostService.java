package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.car.HibernateCarRepository;
import ru.job4j.cars.repository.post.HibernatePostRepository;
import ru.job4j.cars.service.brand.SimpleBrandService;
import ru.job4j.cars.service.engine.SimpleEngineService;
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
    private final SimpleBrandService brandService;
    private final SimpleEngineService engineService;
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
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
            log.warn("Post update failed. Post not found or access denied. postId={}, userId={}", postId, user.getId());
            return Optional.empty();
        }

        Post post = postOptional.get();

        var brandOptional = brandService.findById(brandId);
        var engineOptional = engineService.findById(engineId);
        if (brandOptional.isEmpty() || engineOptional.isEmpty()) {
            log.warn("Post update failed. Invalid brandId={} or engineId={}, postId={}, userId={}",
                    brandId, engineId, postId, user.getId());
            return Optional.empty();
        }
        Car car = post.getCar();
        car.setBrand(brandOptional.get());
        car.setEngine(engineOptional.get());
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
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
            log.warn("Post delete failed. Post not found or access denied. postId={}, userId={}",
                    postId, user.getId());
            return false;
        }

        Post post = postOptional.get();
        Integer carId = post.getCar().getId();
        post.getPhotos().forEach(photo -> fileService.deleteByPath(photo.getPath()));

        postRepository.delete(postId);
        carRepository.delete(carId);
        return true;
    }

    private Optional<Post> findPostByIdAndUser(int postId, User user) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.warn("Post not found. postId={}", postId);
            return Optional.empty();
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
            log.warn("Access denied to post. postId={}, ownerId={}, userId={}",
                    postId, post.getUser().getId(), user.getId());
            return Optional.empty();
        }
        return Optional.of(post);
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
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
            log.warn("Post not found. postId={}", postId);
            return false;
        }
        Post post = postOptional.get();
        post.setSold(true);
        postRepository.update(post);
        return true;
    }
}

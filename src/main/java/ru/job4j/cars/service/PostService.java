package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.PostRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CarRepository carRepository;
    private final BrandService brandService;
    private final EngineService engineService;
    private final FileService fileService;

    public Post create(Post post, User user, MultipartFile file) {
        post.setCreated(LocalDateTime.now());
        post.setUser(user);

        Car savedCar = carRepository.create(post.getCar());
        post.setCar(savedCar);

        addPhoto(file, post);
        return postRepository.create(post);
    }

    public Optional<Post> update(int postId,
                                 Post updatedPost,
                                 Integer brandId,
                                 Integer engineId,
                                 MultipartFile file,
                                 User user) {
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOptional.get();

        var brandOptional = brandService.findById(brandId);
        var engineOptional = engineService.findById(engineId);
        if (brandOptional.isEmpty() || engineOptional.isEmpty()) {
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
            throw new RuntimeException(e);
        }
    }

    public boolean deleteById(int postId, User user) {
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
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
            return Optional.empty();
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
            return Optional.empty();
        }

        return Optional.of(post);
    }

    public List<Post> findAllOrderedById() {
        return postRepository.findAllOrderedById();
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public List<Post> findAllCreatedLastDay() {
        return postRepository.findAllCreatedLastDay();
    }

    public List<Post> findAllWithPhoto() {
        return postRepository.findAllWithPhoto();
    }

    public boolean markAsSold(int postId, User user) {
        Optional<Post> postOptional = findPostByIdAndUser(postId, user);
        if (postOptional.isEmpty()) {
            return false;
        }

        Post post = postOptional.get();
        post.setSold(true);
        postRepository.update(post);
        return true;
    }
}

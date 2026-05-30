package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.car.CarRepository;
import ru.job4j.cars.repository.car.HibernateCarRepository;
import ru.job4j.cars.repository.command.CrudRepository;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.engine.HibernateEngineRepository;
import ru.job4j.cars.repository.post.HibernatePostRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.repository.user.HibernateUserRepository;
import ru.job4j.cars.repository.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostRepositoryTest {

    private static StandardServiceRegistry serviceRegistry;
    private static SessionFactory sessionFactory;
    private static CrudRepository crudRepository;
    private static PostRepository postRepository;
    private static UserRepository userRepository;
    private static CarRepository carRepository;
    private static EngineRepository engineRepository;

    @BeforeAll
    static void setUp() {
        serviceRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(serviceRegistry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        postRepository = new HibernatePostRepository(crudRepository);
        userRepository = new HibernateUserRepository(crudRepository);
        carRepository = new HibernateCarRepository(crudRepository);
        engineRepository = new HibernateEngineRepository(crudRepository);
    }

    @AfterAll
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    @BeforeEach
    void clearDatabase() {
        crudRepository.run(session -> {
            session.createNativeMutationQuery("DELETE FROM photos").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM price_history").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM post_subscribers").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM posts").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM history_owners").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM owners").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM cars").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM engines").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM users").executeUpdate();
        });
    }

    @Test
    void whenCreateThenFindSamePostById() {
        var post = postRepository.create(createPost("Mercedes", "Mercedes description"));
        var result = postRepository.findById(post.getId());

        assertThat(result).isPresent();
        assertThat(result).get().extracting(Post::getDescription).isEqualTo(post.getDescription());
        assertThat(result).get().extracting(p -> post.getUser().getLogin()).isEqualTo("Mercedes_user");
        assertThat(result).get().extracting(p -> post.getCar().getModel()).isEqualTo(post.getCar().getModel());
        assertThat(result).get().extracting(p -> post.getCar().getEngine().getName()).isEqualTo("Mercedes_engine");
    }

    @Test
    void whenCreatePostWithPhotoThenFindSamePostWithPhotoById() {
        var post = postRepository.create(createPost("Mercedes", "Mercedes description"));
        createPhoto(post);
        var result = postRepository.findById(post.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getPhotos()).hasSize(1);
        assertThat(result.get().getPhotos().get(0).getName()).isEqualTo("photo.jpg");
        assertThat(result.get().getPhotos().get(0).getPath()).isEqualTo("files/photo.jpg");
    }

    @Test
    void whenFindByFilterAllThenReturnAllOrderedByIdDesc() {
        var post1 = postRepository.create(createPost("Mercedes", "Mercedes description"));
        var post2 = postRepository.create(createPost("BMW", "BMW description"));
        var result = postRepository.findByFilter(PostFilter.ALL);

        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly(post2.getDescription(), post1.getDescription());
    }

    @Test
    void whenUpdateThenFindUpdatedPostById() {
        var post = postRepository.create(createPost("Mercedes", "Mercedes description"));
        post.setDescription("updated description");
        postRepository.update(post);
        var result = postRepository.findById(post.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo(post.getDescription());
    }

    @Test
    void whenDeleteThenFindByIdReturnEmpty() {
        var post = postRepository.create(createPost("Mercedes", "Mercedes description"));
        postRepository.delete(post.getId());
        var result = postRepository.findById(post.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByFilterTodayThenReturnOnlyNewPosts() {
        var newPost = createPost("Mercedes", "Mercedes description");
        newPost.setCreated(LocalDateTime.now().minusHours(3));
        var oldPost = createPost("BMW", "BMW description");
        oldPost.setCreated(LocalDateTime.now().minusDays(2));
        postRepository.create(newPost);
        postRepository.create(oldPost);
        var result = postRepository.findByFilter(PostFilter.TODAY);

        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly(newPost.getDescription());
    }

    @Test
    void whenFindByFilterWithPhotoThenReturnOnlyPostsWithPhoto() {
        var postWithPhoto = postRepository.create(createPost("Mercedes", "Mercedes description"));
        createPhoto(postWithPhoto);
        postRepository.create(createPost("BMW", "BMW description"));
        var result = postRepository.findByFilter(PostFilter.WITH_PHOTO);

        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly(postWithPhoto.getDescription());

    }

    private Post createPost(String carName, String description) {
        var engine = new Engine();
        engine.setName(carName + "_engine");
        engineRepository.create(engine);

        var car = new Car();
        car.setModel(carName);
        car.setEngine(engine);
        carRepository.create(car);

        var user = new User();
        user.setLogin(carName + "_user");
        user.setPassword("password");
        userRepository.save(user);

        var post = new Post();
        post.setDescription(description);
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        post.setCar(car);

        return post;
    }

    private void createPhoto(Post post) {
        crudRepository.run(session -> session.createNativeMutationQuery(
                        "INSERT INTO photos (name, path, post_id) VALUES (:name, :path, :postId)")
                .setParameter("name", "photo.jpg")
                .setParameter("path", "files/photo.jpg")
                .setParameter("postId", post.getId())
                .executeUpdate());
    }
}

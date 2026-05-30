package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostFilter;
import ru.job4j.cars.model.*;
import ru.job4j.cars.service.brand.BrandService;
import ru.job4j.cars.service.brand.SimpleBrandService;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.engine.SimpleEngineService;
import ru.job4j.cars.service.post.PostService;
import ru.job4j.cars.service.post.SimplePostService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class PostControllerTest {

    private PostService postService;
    private BrandService brandService;
    private EngineService engineService;
    private PostController postController;
    private MultipartFile testFile;
    private User user;
    private Brand brand;
    private Engine engine;

    @BeforeEach
    void setUp() {
        postService = mock(SimplePostService.class);
        brandService = mock(SimpleBrandService.class);
        engineService = mock(SimpleEngineService.class);
        postController = new PostController(postService, brandService, engineService);
        testFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        user = new User(1, "user", "password");
        brand = new Brand();
        brand.setId(1);
        brand.setName("Toyota");
        engine = new Engine();
        engine.setId(1);
        engine.setName("Бензиновый");
    }

    @Test
    void whenRequestAllPostsThenGetListPageWithPosts() {
        var posts = List.of(createPost(1, user));
        when(postService.findByFilter(PostFilter.ALL)).thenReturn(posts);

        var model = new ConcurrentModel();
        var view = postController.getPosts("all", model);
        var actualPosts = model.getAttribute("posts");
        var actualFilter = model.getAttribute("filter");

        assertThat(view).isEqualTo("posts/list");
        assertThat(actualPosts).isEqualTo(posts);
        assertThat(actualFilter).isEqualTo("all");
    }

    @Test
    void whenRequestTodayPostsThenGetListPageWithPosts() {
        var posts = List.of(createPost(1, user));
        when(postService.findByFilter(PostFilter.TODAY)).thenReturn(posts);

        var model = new ConcurrentModel();
        var view = postController.getPosts("today", model);
        var actualPosts = model.getAttribute("posts");
        var actualFilter = model.getAttribute("filter");

        assertThat(view).isEqualTo("posts/list");
        assertThat(actualPosts).isEqualTo(posts);
        assertThat(actualFilter).isEqualTo("today");
    }

    @Test
    void whenRequestPostsWithPhotoThenGetListPageWithPosts() {
        var posts = List.of(createPost(1, user));
        when(postService.findByFilter(PostFilter.WITH_PHOTO)).thenReturn(posts);

        var model = new ConcurrentModel();
        var view = postController.getPosts("with-photo", model);
        var actualPosts = model.getAttribute("posts");
        var actualFilter = model.getAttribute("filter");

        assertThat(view).isEqualTo("posts/list");
        assertThat(actualPosts).isEqualTo(posts);
        assertThat(actualFilter).isEqualTo("with-photo");
    }

    @Test
    void whenRequestPostByIdThenGetDetailsPageWithPost() {
        var post = createPost(1, user);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.getPostById(post.getId(), model);
        var actualPost = model.getAttribute("post");

        assertThat(view).isEqualTo("posts/details");
        assertThat(actualPost).isEqualTo(post);
    }

    @Test
    void whenRequestPostByWrongIdThenGetErrorPageWithMessage() {
        int postId = 1;
        when(postService.findById(postId)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.getPostById(postId, model);
        var actualError = model.getAttribute("error");

        assertThat(view).isEqualTo("error/404");
        assertThat(actualError).isEqualTo("Объявление не найдено.");
    }

    @Test
    void whenRequestCreatePageThenGetPageWithPostBrandsAndEngines() {
        var brands = List.of(brand);
        var engines = List.of(engine);
        when(brandService.findAllOrderByName()).thenReturn(brands);
        when(engineService.findAllOrderById()).thenReturn(engines);

        var model = new ConcurrentModel();
        var view = postController.getCreateForm(model);

        assertThat(view).isEqualTo("posts/create");
        assertThat(model.getAttribute("post")).isInstanceOf(Post.class);
        assertThat(((Post) Objects.requireNonNull(model.getAttribute("post"))).getCar()).isInstanceOf(Car.class);
        assertThat(model.getAttribute("brands")).isEqualTo(brands);
        assertThat(model.getAttribute("engines")).isEqualTo(engines);
    }

    @Test
    void whenCreatePostThenRedirectToPostDetails() {
        var post = createPost(null, user);
        var savedPost = createPost(1, user);
        when(brandService.findById(brand.getId())).thenReturn(Optional.of(brand));
        when(engineService.findById(engine.getId())).thenReturn(Optional.of(engine));
        when(postService.create(post, user, testFile)).thenReturn(savedPost);

        var model = new ConcurrentModel();
        var view = postController.createPost(post, brand.getId(), engine.getId(), testFile, user, model);

        assertThat(view).isEqualTo("redirect:/posts/" + savedPost.getId());
        assertThat(post.getCar().getBrand()).isEqualTo(brand);
        assertThat(post.getCar().getEngine()).isEqualTo(engine);
    }

    @Test
    void whenCreatePostWithWrongBrandThenGetCreatePageWithError() {
        var post = createPost(null, user);
        var brands = List.of(brand);
        var engines = List.of(engine);
        when(brandService.findById(brand.getId())).thenReturn(Optional.empty());
        when(engineService.findById(engine.getId())).thenReturn(Optional.of(engine));
        when(brandService.findAllOrderByName()).thenReturn(brands);
        when(engineService.findAllOrderById()).thenReturn(engines);

        var model = new ConcurrentModel();
        var view = postController.createPost(post, brand.getId(), engine.getId(), testFile, user, model);

        assertThat(view).isEqualTo("posts/create");
        assertThat(model.getAttribute("error")).isEqualTo("Выберите марку или двигатель!");
        assertThat(model.getAttribute("brands")).isEqualTo(brands);
        assertThat(model.getAttribute("engines")).isEqualTo(engines);
    }

    @Test
    void whenRequestEditPageByAuthorThenGetEditPageWithPostBrandsAndEngines() {
        var post = createPost(1, user);
        var brands = List.of(brand);
        var engines = List.of(engine);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));
        when(brandService.findAllOrderByName()).thenReturn(brands);
        when(engineService.findAllOrderById()).thenReturn(engines);

        var model = new ConcurrentModel();
        var view = postController.getEditForm(post.getId(), user, model);

        assertThat(view).isEqualTo("posts/edit");
        assertThat(model.getAttribute("post")).isEqualTo(post);
        assertThat(model.getAttribute("brands")).isEqualTo(brands);
        assertThat(model.getAttribute("engines")).isEqualTo(engines);
    }

    @Test
    void whenRequestEditPageByNotAuthorThenGetErrorPageWithMessage() {
        var author = new User(1, "author", "password");
        var anotherUser = new User(2, "another", "password");
        var post = createPost(1, author);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.getEditForm(post.getId(), anotherUser, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("У вас нет прав на редактирование объявления.");
    }

    @Test
    void whenUpdatePostThenRedirectToPostDetails() {
        var post = createPost(1, user);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));
        when(postService.update(post.getId(), post, brand.getId(), engine.getId(), testFile, user))
                .thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.editPost(post.getId(), post, brand.getId(), engine.getId(), testFile, user, model);

        assertThat(view).isEqualTo("redirect:/posts/" + post.getId());
    }

    @Test
    void whenUpdatePostByWrongIdThenGetErrorPageWithMessage() {
        var post = createPost(1, user);
        when(postService.findById(post.getId())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.editPost(post.getId(), post, brand.getId(), engine.getId(), testFile, user, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("Объявление не найдено.");
    }

    @Test
    void whenUpdatePostByNotAuthorThenGetErrorPageWithMessage() {
        var author = new User(1, "author", "password");
        var anotherUser = new User(2, "another", "password");
        var post = createPost(1, author);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.editPost(post.getId(), post, brand.getId(), engine.getId(), testFile, anotherUser, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("У вас нет прав для редактирования объявления.");
    }

    @Test
    void whenDeletePostThenRedirectToPosts() {
        var post = createPost(1, user);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));
        when(postService.deleteById(post.getId(), user)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = postController.deletePost(post.getId(), user, model);

        assertThat(view).isEqualTo("redirect:/posts");
    }

    @Test
    void whenDeletePostByWrongIdThenGetErrorPageWithMessage() {
        int postId = 1;
        when(postService.findById(postId)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.deletePost(postId, user, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("Объявление не найдено.");
    }

    @Test
    void whenDeletePostByNotAuthorThenGetErrorPageWithMessage() {
        var author = new User(1, "author", "password");
        var anotherUser = new User(2, "another", "password");
        var post = createPost(1, author);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.deletePost(post.getId(), anotherUser, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("У вас нет прав для удаления объявления.");
    }

    @Test
    void whenMarkPostAsSoldThenRedirectToPostDetails() {
        var post = createPost(1, user);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));
        when(postService.markAsSold(post.getId(), user)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = postController.markAsSold(post.getId(), user, model);

        assertThat(view).isEqualTo("redirect:/posts/" + post.getId());
    }

    @Test
    void whenMarkPostAsSoldByWrongIdThenGetErrorPageWithMessage() {
        int postId = 1;
        when(postService.findById(postId)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.markAsSold(postId, user, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("Объявление не найдено.");
    }

    @Test
    void whenMarkPostAsSoldByNotAuthorThenGetErrorPageWithMessage() {
        var author = new User(1, "author", "password");
        var anotherUser = new User(2, "another", "password");
        var post = createPost(1, author);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.markAsSold(post.getId(), anotherUser, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("error")).isEqualTo("У вас нет прав для изменения статуса объявления.");
    }

    private Post createPost(Integer id, User user) {
        var car = new Car();
        car.setBrand(brand);
        car.setEngine(engine);
        car.setModel("Camry");
        car.setBodyType("Седан");

        var post = new Post();
        post.setId(id);
        post.setCar(car);
        post.setUser(user);
        post.setDescription("description");
        return post;
    }
}

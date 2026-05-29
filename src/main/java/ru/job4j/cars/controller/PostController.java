package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.brand.BrandService;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.post.PostService;

import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final BrandService brandService;
    private final EngineService engineService;

    @GetMapping
    public String getPosts(@RequestParam(defaultValue = "all") String filter, Model model) {
        model.addAttribute("posts", postService.findByFilter(filter));
        model.addAttribute("filter", filter);
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable int id, Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
            log.warn("Post details page was requested, but post was not found. postId={}", id);
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }
        model.addAttribute("post", postOptional.get());
        return "posts/details";
    }

    @GetMapping("/create")
    public String getCreateForm(Model model) {
        Post post = new Post();
        post.setCar(new Car());
        model.addAttribute("post", post);
        model.addAttribute("brands", brandService.findAllOrderByName());
        model.addAttribute("engines", engineService.findAllOrderById());
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post,
                             @RequestParam Integer brandId,
                             @RequestParam Integer engineId,
                             @RequestParam MultipartFile file,
                             @SessionAttribute User user,
                             Model model) {
        var brandOptional = brandService.findById(brandId);
        var engineOptional = engineService.findById(engineId);
        if (brandOptional.isEmpty() || engineOptional.isEmpty()) {
            log.warn("Post was not created. Invalid brandId={} or engineId={}, userId={}",
                    brandId, engineId, user.getId());
            model.addAttribute("error", "Выберите марку или двигатель!");
            model.addAttribute("brands", brandService.findAllOrderByName());
            model.addAttribute("engines", engineService.findAllOrderById());
            return "posts/create";
        }
        post.getCar().setBrand(brandOptional.get());
        post.getCar().setEngine(engineOptional.get());
        var savedPost = postService.create(post, user, file);
        return "redirect:/posts/" + savedPost.getId();
    }

    @GetMapping("/edit/{id}")
    public String getEditForm(@PathVariable int id,
                              @SessionAttribute User user,
                              Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
            log.warn("Post edit page was requested, but post was not found. postId={}, userId={}", id, user.getId());
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
            log.warn("Access denied to post edit page. postId={}, ownerId={}, userId={}",
                    post.getId(), post.getUser().getId(), user.getId());
            model.addAttribute("error", "У вас нет прав на редактирование объявления.");
            return "error/404";
        }

        model.addAttribute("post", post);
        model.addAttribute("brands", brandService.findAllOrderByName());
        model.addAttribute("engines", engineService.findAllOrderById());
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String editPost(@PathVariable int id,
                           @ModelAttribute Post post,
                           @RequestParam Integer brandId,
                           @RequestParam Integer engineId,
                           @RequestParam MultipartFile file,
                           @SessionAttribute User user,
                           Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
            log.warn("Post update was requested, but post was not found. postId={}, userId={}", id, user.getId());
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }

        Post existingPost = postOptional.get();
        if (!existingPost.getUser().getId().equals(user.getId())) {
            log.warn("Access denied to post update. postId={}, ownerId={}, userId={}",
                    existingPost.getId(), existingPost.getUser().getId(), user.getId());
            model.addAttribute("error", "У вас нет прав для редактирования объявления.");
            return "error/404";
        }

        Optional<Post> updatedPost = postService.update(id, post, brandId, engineId, file, user);
        if (updatedPost.isEmpty()) {
            model.addAttribute("error", "Выберите марку или двигатель!");
            model.addAttribute("brands", brandService.findAllOrderByName());
            model.addAttribute("engines", engineService.findAllOrderById());
            return "posts/edit";
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable int id,
                             @SessionAttribute User user,
                             Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
            log.warn("Post delete was requested, but post was not found. postId={}, userId={}", id, user.getId());
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
            log.warn("Access denied to post delete. postId={}, ownerId={}, userId={}",
                    post.getId(), post.getUser().getId(), user.getId());
            model.addAttribute("error", "У вас нет прав для удаления объявления.");
            return "error/404";
        }

        postService.deleteById(id, user);
        return "redirect:/posts";
    }

    @PostMapping("/sold/{id}")
    public String markAsSold(@PathVariable int id,
                             @SessionAttribute User user,
                             Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
            log.warn("Post sold status update was requested, but post was not found. postId={}, userId={}", id, user.getId());
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
            log.warn("Access denied to post sold status update. postId={}, ownerId={}, userId={}",
                    post.getId(), post.getUser().getId(), user.getId());
            model.addAttribute("error", "У вас нет прав для изменения статуса объявления.");
            return "error/404";
        }

        postService.markAsSold(id, user);
        return "redirect:/posts/" + id;
    }
}

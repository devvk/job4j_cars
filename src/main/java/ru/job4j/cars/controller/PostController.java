package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.BrandService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.PostService;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final BrandService brandService;
    private final EngineService engineService;

    @GetMapping
    public String getAllPosts(Model model) {
        model.addAttribute("posts", postService.findAllOrderedById());
        return "posts/list";
    }

    @GetMapping("/today")
    public String getPostsCreatedLastDay(Model model) {
        model.addAttribute("posts", postService.findAllCreatedLastDay());
        return "posts/list";
    }

    @GetMapping("/with-photo")
    public String getPostsWithPhoto(Model model) {
        model.addAttribute("posts", postService.findAllWithPhoto());
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable int id, Model model) {
        Optional<Post> postOptional = postService.findById(id);
        if (postOptional.isEmpty()) {
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
            model.addAttribute("error", "Объявление не найдено.");
            return "error/404";
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(user.getId())) {
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
        Optional<Post> updatedPost = postService.update(id, post, brandId, engineId, file, user);
        if (updatedPost.isEmpty()) {
            model.addAttribute("error", "Объявление не найдено или у вас нет прав.");
            return "error/404";
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable int id,
                             @SessionAttribute User user,
                             Model model) {
        boolean isDeleted = postService.deleteById(id, user);
        if (!isDeleted) {
            model.addAttribute("error", "Объявление не найдено или у вас нет прав.");
            return "error/404";
        }
        return "redirect:/posts";
    }

    @PostMapping("/sold/{id}")
    public String markAsSold(@PathVariable int id,
                             @SessionAttribute User user,
                             Model model) {
        boolean isUpdated = postService.markAsSold(id, user);
        if (!isUpdated) {
            model.addAttribute("error", "Объявление не найдено или у вас нет прав.");
            return "error/404";
        }
        return "redirect:/posts/" + id;
    }
}

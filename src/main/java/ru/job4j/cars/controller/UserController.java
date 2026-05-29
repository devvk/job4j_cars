package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.user.UserService;

import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model, HttpSession session) {
        Optional<User> userOptional = userService.save(user);
        if (userOptional.isEmpty()) {
            log.warn("User registration failed. Login already exists: {}", user.getLogin());
            model.addAttribute("error", "Пользователь с таким логином уже существует.");
            model.addAttribute("user", user);
            return "users/register";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/posts";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession session) {
        Optional<User> userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            log.warn("User login failed. login={}", user.getLogin());
            model.addAttribute("error", "Неверный логин или пароль.");
            model.addAttribute("user", user);
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/posts";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            log.info("User logged out. userId={}, login={}", user.getId(), user.getLogin());
        }
        session.invalidate();
        return "redirect:/users/login";
    }
}

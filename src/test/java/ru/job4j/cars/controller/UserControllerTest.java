package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.user.SimpleUserService;
import ru.job4j.cars.service.user.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(SimpleUserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenRequestRegistrationPageThenGetRegistrationView() {
        var model = new ConcurrentModel();

        var view = userController.getRegisterForm(model);
        var user = model.getAttribute("user");

        assertThat(view).isEqualTo("users/register");
        assertThat(user).isInstanceOf(User.class);
    }

    @Test
    void whenRegisterUserThenRedirectToPostsAndSetUserToSession() {
        var user = createUser(1, "user", "password");
        var session = mock(HttpSession.class);
        when(userService.save(user)).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.registerUser(user, model, session);

        assertThat(view).isEqualTo("redirect:/posts");
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenRegisterUserWithExistingLoginThenGetRegisterPageWithError() {
        var user = createUser(1, "user", "password");
        var session = mock(HttpSession.class);
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.registerUser(user, model, session);
        var actualError = model.getAttribute("error");
        var actualUser = model.getAttribute("user");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualError).isEqualTo("Пользователь с таким логином уже существует.");
        assertThat(actualUser).isEqualTo(user);
        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void whenRequestLoginPageThenGetLoginView() {
        var model = new ConcurrentModel();

        var view = userController.getLoginForm(model);
        var user = model.getAttribute("user");

        assertThat(view).isEqualTo("users/login");
        assertThat(user).isInstanceOf(User.class);
    }

    @Test
    void whenLoginUserThenRedirectToPostsAndSetUserToSession() {
        var user = createUser(1, "user", "password");
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.login(user, model, session);

        assertThat(view).isEqualTo("redirect:/posts");
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenLoginWithWrongCredentialsThenGetLoginPageWithError() {
        var user = createUser(null, "wrong", "wrong");
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.login(user, model, session);
        var actualError = model.getAttribute("error");
        var actualUser = model.getAttribute("user");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualError).isEqualTo("Неверный логин или пароль.");
        assertThat(actualUser).isEqualTo(user);
        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void whenLogoutUserThenInvalidateSessionAndRedirectToLoginPage() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
        verify(session).invalidate();
    }

    private User createUser(Integer id, String login, String password) {
        var user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }
}

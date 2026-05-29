package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    void setUp() {
        indexController = new IndexController();
    }

    @Test
    void whenRequestIndexPageThenRedirectToPosts() {
        var view = indexController.index();

        assertThat(view).isEqualTo("redirect:/posts");
    }
}

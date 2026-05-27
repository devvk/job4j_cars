package ru.job4j.cars.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request, Model model) {
        model.addAttribute("error", "Во время обработки запроса произошла ошибка.");
        log.error("Unexpected application error. Method: {}. URI: {}. Query: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                e);
        return "error/500";
    }
}

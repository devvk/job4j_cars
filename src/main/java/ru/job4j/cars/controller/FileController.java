package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.service.PhotoService;

@Controller
@AllArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final PhotoService photoService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable int id) {
        return photoService.findById(id)
                .map(fileDto -> ResponseEntity.ok(fileDto.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }
}

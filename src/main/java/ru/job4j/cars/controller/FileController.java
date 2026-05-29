package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.service.photo.PhotoService;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final PhotoService photoService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable int id) {
        var fileOptional = photoService.findById(id);
        if (fileOptional.isEmpty()) {
            log.warn("File not found. photoId={}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fileOptional.get().getContent());
    }
}


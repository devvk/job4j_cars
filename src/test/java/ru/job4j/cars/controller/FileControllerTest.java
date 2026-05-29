package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.service.photo.PhotoService;
import ru.job4j.cars.service.photo.SimplePhotoService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class FileControllerTest {

    private PhotoService photoService;

    private FileController fileController;

    @BeforeEach
    void setUp() {
        photoService = mock(SimplePhotoService.class);
        fileController = new FileController(photoService);
    }

    @Test
    void whenRequestFileByIdThenGetFileContent() {
        int fileId = 1;
        var fileDto = new FileDto("name", new byte[]{1, 2, 3});
        when(photoService.findById(fileId)).thenReturn(Optional.of(fileDto));

        var view = fileController.getById(fileId);

        assertThat(view).isEqualTo(ResponseEntity.ok(fileDto.getContent()));
    }

    @Test
    void whenRequestFileByWrongIdThenGetNotFound() {
        int fileId = 1;
        when(photoService.findById(fileId)).thenReturn(Optional.empty());

        var view = fileController.getById(fileId);

        assertThat(view).isEqualTo(ResponseEntity.notFound().build());
    }
}

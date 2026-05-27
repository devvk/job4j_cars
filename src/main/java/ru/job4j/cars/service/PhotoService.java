package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.repository.PhotoRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final FileService fileService;

    public Optional<FileDto> findById(int id) {
        return photoRepository.findById(id)
                .map(photo -> fileService.findByPath(photo.getPath()));
    }
}

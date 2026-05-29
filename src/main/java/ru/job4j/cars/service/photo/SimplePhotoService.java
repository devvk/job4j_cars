package ru.job4j.cars.service.photo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.repository.photo.HibernatePhotoRepository;
import ru.job4j.cars.service.file.SimpleFileService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePhotoService implements PhotoService {

    private final HibernatePhotoRepository photoRepository;
    private final SimpleFileService fileService;

    @Override
    public Optional<FileDto> findById(int id) {
        return photoRepository.findById(id)
                .map(photo -> fileService.findByPath(photo.getPath()));
    }
}

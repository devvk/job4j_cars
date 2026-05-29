package ru.job4j.cars.service.photo;

import ru.job4j.cars.dto.FileDto;

import java.util.Optional;

public interface PhotoService {

    Optional<FileDto> findById(int id);
}

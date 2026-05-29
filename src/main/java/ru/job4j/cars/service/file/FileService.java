package ru.job4j.cars.service.file;

import ru.job4j.cars.dto.FileDto;

public interface FileService {

    String save(FileDto fileDto);

    FileDto findByPath(String path);

    void deleteByPath(String path);
}

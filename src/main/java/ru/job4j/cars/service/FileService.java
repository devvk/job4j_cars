package ru.job4j.cars.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileService {

    private final String storageDirectory;

    public FileService(@Value("${file.directory}") String storageDirectory) {
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    public String save(FileDto fileDto) {
        String path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return path;
    }

    public FileDto findByPath(String path) {
        return new FileDto(Path.of(path).getFileName().toString(), readFileBytes(path));
    }

    public void deleteByPath(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createStorageDirectory(String storageDirectory) {
        try {
            Files.createDirectories(Path.of(storageDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewFilePath(String sourceName) {
        return Path.of(storageDirectory, UUID.randomUUID() + "_" + sourceName).toString();
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFileBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package ru.job4j.cars.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
public class SimpleFileService implements FileService {

    private final String storageDirectory;

    public SimpleFileService(@Value("${file.directory}") String storageDirectory) {
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    @Override
    public String save(FileDto fileDto) {
        String path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return path;
    }

    @Override
    public FileDto findByPath(String path) {
        return new FileDto(Path.of(path).getFileName().toString(), readFileBytes(path));
    }

    @Override
    public void deleteByPath(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            log.error("Error while deleting file. path={}", path, e);
            throw new RuntimeException(e);
        }
    }

    private void createStorageDirectory(String storageDirectory) {
        try {
            Files.createDirectories(Path.of(storageDirectory));
        } catch (IOException e) {
            log.error("Error while creating storage directory. path={}", storageDirectory, e);
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
            log.error("Error while writing file. path={}", path, e);
            throw new RuntimeException(e);
        }
    }

    private byte[] readFileBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            log.error("Error while reading file. path={}", path, e);
            throw new RuntimeException(e);
        }
    }
}

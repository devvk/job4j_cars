package ru.job4j.cars.repository.photo;

import ru.job4j.cars.model.Photo;

import java.util.Optional;

public interface PhotoRepository {

    Optional<Photo> findById(int id);
}

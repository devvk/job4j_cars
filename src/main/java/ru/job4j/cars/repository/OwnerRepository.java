package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.repository.command.CrudRepository;

@Repository
@AllArgsConstructor
public class OwnerRepository {

    private final CrudRepository crudRepository;
}

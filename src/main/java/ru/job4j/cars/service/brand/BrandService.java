package ru.job4j.cars.service.brand;

import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<Brand> findAllOrderByName();

    Optional<Brand> findById(Integer id);
}

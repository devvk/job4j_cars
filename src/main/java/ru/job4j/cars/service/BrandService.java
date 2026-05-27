package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.BrandRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> findAllOrderByName() {
        return brandRepository.findAllOrderByName();
    }

    public Optional<Brand> findById(Integer id) {
        return brandRepository.findById(id);
    }
}

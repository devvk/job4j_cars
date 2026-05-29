package ru.job4j.cars.service.brand;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.brand.HibernateBrandRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleBrandService implements BrandService {

    private final HibernateBrandRepository hibernateBrandRepository;

    @Override
    public List<Brand> findAllOrderByName() {
        return hibernateBrandRepository.findAllOrderByName();
    }

    @Override
    public Optional<Brand> findById(Integer id) {
        return hibernateBrandRepository.findById(id);
    }
}

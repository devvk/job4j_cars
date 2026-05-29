package ru.job4j.cars.service.engine;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.engine.HibernateEngineRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleEngineService implements EngineService {

    private final HibernateEngineRepository engineRepository;

    @Override
    public Engine create(Engine engine) {
        return engineRepository.create(engine);
    }

    @Override
    public void update(Engine engine) {
        engineRepository.update(engine);
    }

    @Override
    public void delete(Integer engineId) {
        engineRepository.delete(engineId);
    }

    @Override
    public List<Engine> findAllOrderById() {
        return engineRepository.findAllOrderById();
    }

    @Override
    public Optional<Engine> findById(Integer engineId) {
        return engineRepository.findById(engineId);
    }
}

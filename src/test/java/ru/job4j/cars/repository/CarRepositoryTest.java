package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.car.CarRepository;
import ru.job4j.cars.repository.car.HibernateCarRepository;
import ru.job4j.cars.repository.command.CrudRepository;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.engine.HibernateEngineRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CarRepositoryTest {

    private static StandardServiceRegistry serviceRegistry;
    private static SessionFactory sessionFactory;
    private static CrudRepository crudRepository;
    private static CarRepository carRepository;
    private static EngineRepository engineRepository;

    @BeforeAll
    static void setUp() {
        serviceRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(serviceRegistry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        carRepository = new HibernateCarRepository(crudRepository);
        engineRepository = new HibernateEngineRepository(crudRepository);
    }

    @AfterAll
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    @BeforeEach
    void clearDatabase() {
        crudRepository.run(session -> {
            session.createNativeMutationQuery("DELETE FROM photos").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM price_history").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM post_subscribers").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM posts").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM history_owners").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM owners").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM cars").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM engines").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM users").executeUpdate();
        });
    }

    @Test
    void whenCreateThenFindSameCarById() {
        var car = carRepository.create(createCar("Mercedes", "V8"));
        var result = carRepository.findById(car.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getModel()).isEqualTo(car.getModel());
        assertThat(result.get().getEngine()).isEqualTo(car.getEngine());
    }

    @Test
    void whenCreateSeveralThenFindAllOrderedById() {
        carRepository.create(createCar("Mercedes", "V8"));
        carRepository.create(createCar("BMW", "V10"));
        var result = carRepository.findAllOrderById();

        assertThat(result).extracting(Car::getModel).containsExactly("Mercedes", "BMW");
    }

    @Test
    void whenUpdateThenFindUpdatedCarById() {
        var car = carRepository.create(createCar("Mercedes", "V8"));
        car.setModel("Mercedes Updated");
        carRepository.update(car);
        var result = carRepository.findById(car.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getModel()).isEqualTo(car.getModel());
    }

    @Test
    void whenDeleteThenFindByIdReturnEmpty() {
        var car = carRepository.create(createCar("Mercedes", "V8"));
        carRepository.delete(car.getId());
        var result = carRepository.findById(car.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByEngineIdThenReturnOnlyCarsWithSameEngine() {
        var car1 = carRepository.create(createCar("Mercedes", "V8"));
        carRepository.create(createCar("BMW", "V10"));
        var result = carRepository.findByEngineId(car1.getEngine().getId());

        assertThat(result).extracting(Car::getModel).containsExactly(car1.getModel());
    }

    private Car createCar(String carName, String engineName) {
        var engine = new Engine();
        engine.setName(engineName);
        engineRepository.create(engine);

        var car = new Car();
        car.setModel(carName);
        car.setEngine(engine);
        return car;
    }
}
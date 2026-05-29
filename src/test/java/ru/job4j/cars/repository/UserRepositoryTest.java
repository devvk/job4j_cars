package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.command.CrudRepository;
import ru.job4j.cars.repository.user.HibernateUserRepository;
import ru.job4j.cars.repository.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private static StandardServiceRegistry serviceRegistry;
    private static SessionFactory sessionFactory;
    private static CrudRepository crudRepository;
    private static UserRepository userRepository;

    @BeforeAll
    static void setUp() {
        serviceRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(serviceRegistry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        userRepository = new HibernateUserRepository(crudRepository);
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
    void whenCreateThenFindSameUserById() {
        var savedUser = userRepository.save(createUser("user")).orElseThrow();
        var foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        assertThat(foundUser.getLogin()).isEqualTo(savedUser.getLogin());
        assertThat(foundUser.getPassword()).isEqualTo(savedUser.getPassword());
    }

    @Test
    public void whenCreateSeveralUserFindAllOrderById() {
        var user1 = userRepository.save(createUser("user1")).orElseThrow();
        var user2 = userRepository.save(createUser("user2")).orElseThrow();
        var result = userRepository.findAllOrderById();

        assertThat(result)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    void whenUpdateThenFindUpdatedUser() {
        var user = userRepository.save(createUser("user")).orElseThrow();
        user.setLogin("updated");
        user.setPassword("updated");
        userRepository.update(user);
        var result = userRepository.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result).get().extracting(User::getLogin).isEqualTo(user.getLogin());
        assertThat(result).get().extracting(User::getPassword).isEqualTo(user.getPassword());
    }

    @Test
    void whenDeleteThenFindByIdReturnEmpty() {
        var user = userRepository.save(createUser("user")).orElseThrow();
        userRepository.delete(user.getId());
        var result = userRepository.findById(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByLikeLoginThenReturnMatchedUser() {
        var user1 = userRepository.save(createUser("user1")).orElseThrow();
        var user2 = userRepository.save(createUser("user2")).orElseThrow();
        userRepository.save(createUser("not found"));
        var result = userRepository.findByLikeLogin("user");

        assertThat(result)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    void whenFindByLoginThenReturnSameUser() {
        var user = userRepository.save(createUser("user")).orElseThrow();
        var result = userRepository.findByLogin(user.getLogin());

        assertThat(result).isPresent();
        assertThat(result).get().extracting(User::getLogin).isEqualTo(user.getLogin());
    }

    private User createUser(String login) {
        var user = new User();
        user.setLogin(login);
        user.setPassword("password");
        return user;
    }
}
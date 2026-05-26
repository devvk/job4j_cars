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
        userRepository = new UserRepository(crudRepository);
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
        var user = userRepository.create(createUser("user"));
        var result = userRepository.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result).get().extracting(User::getLogin).isEqualTo(user.getLogin());
        assertThat(result).get().extracting(User::getPassword).isEqualTo(user.getPassword());
    }

    @Test
    public void whenCreateSeveralUserFindAllOrderById() {
        var user1 = userRepository.create(createUser("user1"));
        var user2 = userRepository.create(createUser("user2"));
        var result = userRepository.findAllOrderById();

        assertThat(result)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    void whenUpdateThenFindUpdatedUser() {
        var user = userRepository.create(createUser("user"));
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
        var user = userRepository.create(createUser("user"));
        userRepository.delete(user.getId());
        var result = userRepository.findById(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByLikeLoginThenReturnMatchedUser() {
        var user1 = userRepository.create(createUser("user1"));
        var user2 = userRepository.create(createUser("user2"));
        userRepository.create(createUser("not found"));
        var result = userRepository.findByLikeLogin("user");

        assertThat(result)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    void whenFindByLoginThenReturnSameUser() {
        var user = userRepository.create(createUser("user"));
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
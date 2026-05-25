package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
@AllArgsConstructor
@SuppressWarnings("SqlSourceToSinkFlow")
public class CrudRepository {

    private final SessionFactory sessionFactory;

    private <T> T transaction(Function<Session, T> command) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void run(Consumer<Session> command) {
        transaction(session -> {
            command.accept(session);
            return null;
        });
    }

    public void run(String query, Map<String, Object> args) {
        Consumer<Session> command = session -> {
            var sessionQuery = session.createMutationQuery(query);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            sessionQuery.executeUpdate();
        };
        run(command);
    }

    public <T> List<T> query(String query, Class<T> clazz) {
        Function<Session, List<T>> command = session ->
                session.createQuery(query, clazz).list();
        return transaction(command);
    }

    public <T> List<T> query(String query, Class<T> clazz, Map<String, Object> args) {
        Function<Session, List<T>> command = session -> {
            var sessionQuery = session.createQuery(query, clazz);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return sessionQuery.list();
        };
        return transaction(command);
    }

    public <T> Optional<T> optional(String query, Class<T> clazz, Map<String, Object> args) {
        Function<Session, Optional<T>> command = session -> {
            var sessionQuery = session.createQuery(query, clazz);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return sessionQuery.uniqueResultOptional();
        };
        return transaction(command);
    }
}

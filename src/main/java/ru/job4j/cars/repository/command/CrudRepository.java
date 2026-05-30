package ru.job4j.cars.repository.command;

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
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void run(Consumer<Session> command) {
        transaction(session -> {
            command.accept(session);
            return null;
        });
    }

    public void run(String hql, Map<String, Object> args) {
        Consumer<Session> command = session -> {
            var sessionQuery = session.createMutationQuery(hql);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            sessionQuery.executeUpdate();
        };
        run(command);
    }

    public <T> List<T> query(String hql, Class<T> clazz) {
        Function<Session, List<T>> command = session ->
                session.createQuery(hql, clazz).list();
        return transaction(command);
    }

    public <T> List<T> query(String hql, Class<T> clazz, Map<String, Object> args) {
        Function<Session, List<T>> command = session -> {
            var sessionQuery = session.createQuery(hql, clazz);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return sessionQuery.list();
        };
        return transaction(command);
    }

    public <T> T query(Function<Session, T> command) {
        return transaction(command);
    }

    public <T> Optional<T> optional(String hql, Class<T> clazz, Map<String, Object> args) {
        Function<Session, Optional<T>> command = session -> {
            var sessionQuery = session.createQuery(hql, clazz);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                sessionQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return sessionQuery.uniqueResultOptional();
        };
        return transaction(command);
    }
}

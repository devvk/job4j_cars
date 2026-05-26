package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "history_owners",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id")
    )
    private Set<Owner> owners = new HashSet<>();
}

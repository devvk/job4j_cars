package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "engines")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;
}

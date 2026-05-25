package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "engines")
@Data
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
}

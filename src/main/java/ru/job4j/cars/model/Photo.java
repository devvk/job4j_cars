package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "photos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String name;

    private String path;
}
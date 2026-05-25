package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "owners")
@Data
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

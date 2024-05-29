package com.wayev.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "vertices")
public class Vertex {
    @Id
    private String id;

    @Column
    private String label;

    @Column
    private double x;

    @Column
    private double y;

    // Конструктор, геттеры, сеттеры, equals, hashCode и toString будут сгенерированы автоматически
}
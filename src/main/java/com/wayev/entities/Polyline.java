package com.wayev.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "polylines")
public class Polyline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "edge_id")
    private Edge edge;

    @Column
    private double longitude;

    @Column
    private double latitude;

    @Column
    private int order;
}

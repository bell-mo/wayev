package com.wayev.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "edges")
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Vertex source;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Vertex destination;

    @Column
    private double distance;

    @Column
    private double duration;

}
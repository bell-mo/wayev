package com.wayev.repositories;

import com.wayev.entities.Edge;
import com.wayev.entities.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    Optional<Edge> findBySourceAndDestination(Vertex source, Vertex destination);
}


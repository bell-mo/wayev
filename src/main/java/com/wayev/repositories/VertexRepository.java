package com.wayev.repositories;

import com.wayev.entities.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VertexRepository extends JpaRepository<Vertex, String> {
}


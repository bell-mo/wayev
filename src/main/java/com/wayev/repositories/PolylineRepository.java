package com.wayev.repositories;

import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolylineRepository extends JpaRepository<Polyline, Long> {

    List<Polyline> findAllByEdge(Edge edge);

}

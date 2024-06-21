package com.wayev.services;

import com.wayev.classes.Graph;
import com.wayev.classes.MapboxResponseDto;
import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;
import com.wayev.entities.Vertex;
import com.wayev.repositories.EdgeRepository;
import com.wayev.repositories.PolylineRepository;
import com.wayev.repositories.VertexRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WayFindingService {

    @Autowired
    VertexRepository vertexRepository;

    @Autowired
    EdgeRepository edgeRepository;

    @Autowired
    PolylineRepository polylineRepository;

    @Autowired
    GraphService graphService;

    public void setGraph(Graph graph) {
        graph.setVertices(vertexRepository.findAll());
        List<Edge> edges = edgeRepository.findAll()
                .stream()
                .filter(edge -> edge.getDistance() < graph.getRange())
                .toList();
        graph.setEdges(edges);
        edges.forEach(edge -> {
            List<Polyline> polyline = polylineRepository.findAllByEdge(edge);
            graph.addPolyline(edge.getId(), polyline);
        });
    }

    public void addWaypointsToGraph(Graph graph) {
        graph.getVertices().forEach(vertex -> {
            if (!vertex.getId().equals(graph.getStart().getId())) {
                //System.out.println(graph.getStart());
                //System.out.println(vertex);
                MapboxResponseDto response = graphService.getEdgeFromMapbox(graph.getStart(), vertex);
                if (response.edge().getDistance() < graph.getRange() * graph.getStartCharge() / 100.0) {
                    Edge edge = response.edge();
                    edge.setId((long) graph.getEdges().size());
                    graph.addEdge(edge);
                    graph.addPolyline(response.edge().getId(), response.polyline());
                }
            }
        });
        graph.getVertices().forEach(vertex -> {
            if (!vertex.getId().equals(graph.getEnd().getId())) {
                MapboxResponseDto response = graphService.getEdgeFromMapbox(vertex, graph.getEnd());
                if (response.edge().getDistance() < graph.getRange()) {
                    Edge edge = response.edge();
                    edge.setId((long) graph.getEdges().size());
                    graph.addEdge(edge);
                    graph.addPolyline(response.edge().getId(), response.polyline());
                }
            }
        });
    }

    public void addConditions(Vertex current, Double p1, Set<String> passedVertices, Graph graph) { //вызывается в коде для current = end, p1 = endCharge
        List<Vertex> sources = graph.getAdjacentVerticesReverse(current); // находим все вершины-источники (из которых есть путь до текущей)
        for (Vertex source : sources) {
            Edge edge = graph.getEdge(source, current);
            //System.out.println(source);
            //System.out.println(current);
            //System.out.println(edge);
            double p1Source;
            if (current.getId().equals(graph.getEnd().getId())) { // если текущая вершина - конечная, то веса ребер не меняем - на конечной не нужно заряжаться
                p1Source = Math.max((edge.getDistance() / graph.getRange() * 100.0) + p1, 100.0);
            } else if (!source.getId().equals(graph.getStart().getId())) { // если текущая вершина - зарядная станция
                p1Source = Math.max((edge.getDistance() / graph.getRange() * 100.0) / 2.0 + 60.0, 100.0);
                double p0Current = p1Source - (edge.getDistance() / graph.getRange() * 100.0);
                double duration = (edge.getDuration() + findTime(p0Current, p1)) * findI(p0Current, p1);
                graph.addChargeTime(current.getId(), findTime(p0Current, p1));
                graph.addChargeLevel(current.getId(), p1);
                edge.setDuration(duration);
                graph.setEdge(edge);
            } else { // если вершина-источник это стартовая вершина
                p1Source = 0;
                double p0Current = graph.getStartCharge() - (edge.getDistance() / graph.getRange() * 100.0);
                double duration = (edge.getDuration() + findTime(p0Current, p1)) * findI(p0Current, p1);
                graph.addChargeTime(current.getId(), findTime(p0Current, p1));
                graph.addChargeLevel(current.getId(), p1);
                edge.setDuration(duration);
                graph.setEdge(edge);
            }
            if (!source.getId().equals(graph.getStart().getId()) && !passedVertices.contains(source.getId())) {
                passedVertices.add(current.getId());
                addConditions(source, p1Source, passedVertices, graph); // для каждой вершины-источника вызываем себя же
            }
        }
    }

    public Double findI(Double p0, Double p1) {
        return Math.pow((p1 - 60), 3) / 280000.0 - Math.pow((p0 - 60), 3) / 280000.0 + 1;
    }

    public Double findTime(Double p0, Double p1) {
        double C = 80;
        double W = 80;
        if (p0 < 80.0 && p1 > 80) {
            return ((C * (80.0 - p0) / (100.0 * W)) + (C * (p1 - 80.0) / (20.0 * W))) * 3600.0;
        } else if (p1 <= 80) {
            return (C * (p1 - p0) / (100.0 * W)) * 3600.0;
        } else {
            return (C * (p1 - p0) / (20.0 * W)) * 3600.0;
        }
    }
}

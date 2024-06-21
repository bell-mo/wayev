package com.wayev.controllers;

import com.wayev.classes.FindWayRequest;
import com.wayev.classes.FindWayResponse;
import com.wayev.classes.Graph;
import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;
import com.wayev.entities.Vertex;
import com.wayev.repositories.EdgeRepository;
import com.wayev.services.GraphService;
import com.wayev.services.WayFindingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class WayFindingController {

    @Autowired
    private final WayFindingService wayFindingService;

    @Autowired
    private final EdgeRepository edgeRepository;

    @Autowired
    private final GraphService graphService;

    @Operation(description = "Нахождение пути")
    @PostMapping("api/way")
    public FindWayResponse findWay(@RequestBody FindWayRequest findWayRequest) {
        System.out.println(findWayRequest);

        Graph graph = new Graph();
        Vertex start = new Vertex();
        Vertex end = new Vertex();
        FindWayResponse response = new FindWayResponse();
        Set<String> passedVertices = new HashSet<>();

        graph.setRange(findWayRequest.getRange() * 1000.0);

        wayFindingService.setGraph(graph);

        graph.setStartCharge(findWayRequest.getStartCharge());
        graph.setEndCharge(findWayRequest.getEndCharge());

        start.setId("start");
        start.setLongitude(findWayRequest.getStartPos().getCoordinates().get(0));
        start.setLatitude(findWayRequest.getStartPos().getCoordinates().get(1));
        graph.setStart(start);

        end.setId("end");
        end.setLongitude(findWayRequest.getEndPos().getCoordinates().get(0));
        end.setLatitude(findWayRequest.getEndPos().getCoordinates().get(1));
        graph.setEnd(end);

        wayFindingService.addWaypointsToGraph(graph);
        wayFindingService.addConditions(graph.getEnd(), graph.getEndCharge(), passedVertices, graph);

        List<Vertex> way = graph.aStar(graph.getStart(), graph.getEnd());

        FindWayResponse.Geometry geometry = new FindWayResponse.Geometry();
        List<List<Double>> wayCoords = new ArrayList<>();
        List<FindWayResponse.Charge> stations = new ArrayList<>();

        double time = 0.0;
        double length = 0.0;

        for (Vertex vertex : way) {
            FindWayResponse.Charge station = new FindWayResponse.Charge();
            if (!vertex.getId().equals("start") && !vertex.getId().equals("end")) {
                station.setName(vertex.getLabel());
                station.setAddress(vertex.getAddress());
                station.setChargeLevel(graph.getChargeLevel(vertex.getId()));

                List<Double> coordinates = new ArrayList<>();
                coordinates.add(vertex.getLongitude());
                coordinates.add(vertex.getLatitude());
                station.setCoordinates(coordinates);

                time += graph.getChargeTime(vertex.getId());
                stations.add(station);
                System.out.println(station);
            }
        }

        response.setStations(stations);

        for (int i = 0; i < way.size() - 1; i++) {
            Edge edge = graph.getEdge(way.get(i), way.get(i + 1));
            //Edge repoEdge = edgeRepository.findBySourceAndDestination(way.get(i), way.get(i + 1)).get();
            graph.getPolylines().get(edge.getId()).forEach(polyline -> {
                wayCoords.add(new ArrayList<>(Arrays.asList(polyline.getLongitude(), polyline.getLatitude())));
                System.out.println(polyline);
            });
            time += edge.getDuration();
            length += edge.getDistance();
            System.out.println(edge);
        }
        geometry.setCoordinates(wayCoords);
        response.setGeometry(geometry);
        response.setTime(time);
        response.setLength(length);

        return response;
    }

    @Operation(description = "Построение связей")
    @PatchMapping("/api/edges")
    public void setEdges() {
        graphService.saveEdges();
    }
}

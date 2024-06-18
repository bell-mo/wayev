package com.wayev.services;

import com.wayev.classes.MapboxResponse;
import com.wayev.classes.MapboxResponseDto;
import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;
import com.wayev.entities.Vertex;
import com.wayev.repositories.EdgeRepository;
import com.wayev.repositories.PolylineRepository;
import com.wayev.repositories.VertexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GraphService {

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private PolylineRepository polylineRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mapbox.api.url}")
    private String mapboxApiUrl;

    @Value("${mapbox.api.token}")
    private String mapboxApiToken;

    public void saveEdges() {
        List<Vertex> vertices = vertexRepository.findAll();

        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                Vertex source = vertices.get(i);
                Vertex destination = vertices.get(j);
                Optional<Edge> currentEdge = edgeRepository.findBySourceAndDestination(source, destination);

                if (currentEdge.isEmpty() && !Objects.equals(source.getId(), destination.getId())) {
                    MapboxResponseDto response = getEdgeFromMapbox(source, destination);
                    assert response != null;
                    edgeRepository.save(response.edge());
                    for (int k = 0; k < response.polyline().size(); k++) {
                        Polyline polyline = new Polyline();
                        polyline.setEdge(response.edge());
                        polyline.setLongitude(response.polyline().get(k).getLongitude());
                        polyline.setLatitude(response.polyline().get(k).getLatitude());
                        polyline.setOrder(k);
                        polylineRepository.save(polyline);
                    }
                }
            }
        }
    }

    public MapboxResponseDto getEdgeFromMapbox(Vertex source, Vertex destination) {
        String coordinates = source.getLongitude() + "," + source.getLatitude() + ";" + destination.getLongitude() + "," + destination.getLatitude();
        String url = String.format("%s/%s?access_token=%s", mapboxApiUrl, coordinates, mapboxApiToken);

        MapboxResponse response = restTemplate.getForObject(url, MapboxResponse.class);
        if (response != null && !response.getRoutes().isEmpty()) {
            Edge edge = new Edge();
            edge.setSource(source);
            edge.setDestination(destination);
            edge.setDistance(response.getRoutes().getFirst().getDistance());
            edge.setDuration(response.getRoutes().getFirst().getDuration());

            List<Polyline> polyline = new ArrayList<>();

            response.getRoutes().getFirst().getGeometry().getCoordinates()
                    .forEach(point -> {
                        Polyline polylinePoint = new Polyline();
                        polylinePoint.setLongitude(point.get(0));
                        polylinePoint.setLatitude(point.get(1));
                        polyline.add(polylinePoint);
                    });
            return new MapboxResponseDto(edge, polyline);
        } else {
            return null;
        }
    }
}


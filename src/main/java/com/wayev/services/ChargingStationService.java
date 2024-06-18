package com.wayev.services;

import com.wayev.entities.Vertex;
import com.wayev.repositories.VertexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargingStationService {

    @Autowired
    private MapboxService mapboxService;

    @Autowired
    private VertexRepository vertexRepository;

    public void saveChargingStations(String city, String country) {
        List<Vertex> chargingStations = mapboxService.getChargingStations(city, country);
        vertexRepository.saveAll(chargingStations);
    }
}
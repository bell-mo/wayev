package com.wayev.services;

import com.wayev.classes.MapboxGeocodingResponse;
import com.wayev.entities.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MapboxService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mapbox.api.geocoding.url}")
    private String mapboxApiGeocodingUrl;

    @Value("${mapbox.api.token}")
    private String mapboxApiToken;

    public List<Vertex> getChargingStations(String city, String country) {
        String query = "charging station " + city + (country != null ? ", " + country : "");
        String url = String.format("%s/%s.json?access_token=%s", mapboxApiGeocodingUrl, query, mapboxApiToken);

        MapboxGeocodingResponse response = restTemplate.getForObject(url, MapboxGeocodingResponse.class);
        if (response != null && response.getFeatures() != null) {
            return response.getFeatures().stream().map(feature -> {
                Vertex vertex = new Vertex();
                vertex.setId(feature.getId());
                vertex.setLabel(feature.getPlaceName());
                vertex.setLatitude(feature.getGeometry().getCoordinates().get(1));
                vertex.setLongitude(feature.getGeometry().getCoordinates().get(0));
                return vertex;
            }).toList();
        } else {
            return List.of();
        }
    }
}

package com.wayev.classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapboxResponse {

    @JsonProperty("routes")
    private List<Route> routes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("distance")
        private double distance;

        @JsonProperty("duration")
        private double duration;

        @JsonProperty("geometry")
        private Geometry geometry;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Geometry {
            @JsonProperty("coordinates")
            private List<List<Double>> coordinates;
        }
    }
}
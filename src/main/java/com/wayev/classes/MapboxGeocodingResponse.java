package com.wayev.classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapboxGeocodingResponse {

    @JsonProperty("features")
    private List<Feature> features;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        @JsonProperty("id")
        private String id;

        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("geometry")
        private Geometry geometry;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Geometry {
            @JsonProperty("coordinates")
            private List<Double> coordinates;
        }
    }
}

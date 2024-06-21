package com.wayev.classes;

import lombok.Data;

import java.util.List;

@Data
public class FindWayResponse {
    private Geometry geometry;

    @Data
    public static class Geometry {
        List<List<Double>> coordinates;
    }

    private Double time;

    private Double length;

    private List<Charge> stations;

    @Data
    public static class Charge {
        String name;

        String address;

        Double chargeLevel;

        List<Double> coordinates;
    }
}

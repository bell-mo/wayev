package com.wayev.classes;

import lombok.Data;
import java.util.List;

@Data
public class FindWayRequest {
    private Point startPos;

    private Point endPos;

    @Data
    public static class Point {
        List<Double> coordinates;
    }

    private Double startCharge;

    private Double endCharge;

    private Double range;
}

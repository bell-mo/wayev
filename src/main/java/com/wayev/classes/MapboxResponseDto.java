package com.wayev.classes;

import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;

import java.util.List;

public record MapboxResponseDto(
        Edge edge,
        List<Polyline> polyline
) {
}

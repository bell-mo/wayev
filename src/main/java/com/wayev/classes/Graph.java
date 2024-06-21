package com.wayev.classes;

import com.wayev.entities.Edge;
import com.wayev.entities.Polyline;
import com.wayev.entities.Vertex;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.*;

@Data
@NoArgsConstructor
public class Graph {
    @NonNull
    private List<Vertex> vertices = new ArrayList<>();
    @NonNull
    private List<Edge> edges = new ArrayList<>();
    @NonNull
    private Map<Long, List<Polyline>> polylines = new HashMap<>();
    @NonNull
    private Map<String, Double> chargeLevels = new HashMap<>();
    @NonNull
    private Map<String, Double> chargeTimes = new HashMap<>();

    Double startCharge;

    Double endCharge;

    Vertex start;

    Vertex end;

    Double range;

    // Добавление вершины
    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    // Добавление ребра
    public void addEdge(Edge edge) {
        List<Edge> newEdges = new ArrayList<>(edges);
        newEdges.add(edge);
        edges = newEdges;
    }

    public void addPolyline(Long edgeId, List<Polyline> polyline) {
        polylines.put(edgeId, polyline);
    }

    public void addChargeLevel(String vertexId, Double chargeLevel) { chargeLevels.put(vertexId, chargeLevel); }

    public Double getChargeLevel(String vertexId) { return chargeLevels.get(vertexId); }

    public void addChargeTime(String vertexId, Double chargeTime) { chargeTimes.put(vertexId, chargeTime); }

    public Double getChargeTime(String vertexId) { return chargeTimes.get(vertexId); }

    private int findEdgeIndex(Edge edge) {
        for (int i = 0; i < edges.size(); i++) {
            if (edge.getId().equals(edges.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public void setEdge(Edge edge) {
        edges.set(findEdgeIndex(edge), edge);
    }

    // Получение списка смежных вершин для данной вершины
    public List<Vertex> getAdjacentVertices(Vertex vertex) {
        List<Vertex> adjacentVertices = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(vertex)) {
                adjacentVertices.add(edge.getDestination());
            }
        }
        return adjacentVertices;
    }

    public List<Vertex> getAdjacentVerticesReverse(Vertex vertex) {
        List<Vertex> adjacentVertices = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getDestination().equals(vertex)) {
                adjacentVertices.add(edge.getSource());
            }
        }
        return adjacentVertices;
    }

    // Получение веса ребра между двумя вершинами
    public double getEdgeWeight(Vertex source, Vertex destination) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(destination)) {
                return edge.getDuration();
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public Edge getEdge(Vertex source, Vertex destination) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(destination)) {
                return edge;
            }
        }
        return null;
    }

    // Эвристическая функция (евклидово расстояние)
    private double heuristic(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v1.getLongitude() - v2.getLongitude(), 2) + Math.pow(v1.getLatitude() - v2.getLatitude(), 2));
    }

    // Реализация алгоритма A*
    public List<Vertex> aStar(Vertex start, Vertex goal) {
        Map<Vertex, Vertex> cameFrom = new HashMap<>();
        Map<Vertex, Double> gScore = new HashMap<>();
        Map<Vertex, Double> fScore = new HashMap<>();
        PriorityQueue<Vertex> openSet = new PriorityQueue<>(Comparator.comparingDouble(v -> fScore.getOrDefault(v, Double.POSITIVE_INFINITY)));

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Vertex current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            for (Vertex neighbor : getAdjacentVertices(current)) {
                double tentativeGScore = gScore.getOrDefault(current, Double.POSITIVE_INFINITY) + getEdgeWeight(current, neighbor);

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, goal));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Путь не найден
        return Collections.emptyList();
    }

    // Восстановление пути
    private List<Vertex> reconstructPath(Map<Vertex, Vertex> cameFrom, Vertex current) {
        List<Vertex> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        Collections.reverse(totalPath);
        return totalPath;
    }
}

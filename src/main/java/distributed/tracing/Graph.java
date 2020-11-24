package distributed.tracing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    /**
     * Mapping Edges to `from` and `to` Microservices
     */
    private final Map<Integer, Map<Integer, Edge>> matrix = new HashMap<>();

    public Graph(String route) {
        initializeGraph(route);
    }

    private void initializeGraph(String routes) {
        String[] inputArr = routes.split(",");
        for (String trace : inputArr) {
            Edge edge = buildEdge(trace);
            matrix.compute(edge.fromIdx, (integer, edges) -> {
                edges = edges != null ? edges : new HashMap<>();
                Edge oldEdge = edges.put(edge.toIdx, Edge.of(edge.fromIdx, edge.toIdx, edge.weight));
                if (oldEdge != null) {
                    throw new IllegalArgumentException("Trace is duplicated: " + trace);
                }
                return edges;
            });
        }
    }

    private Edge buildEdge(String trace) {
        String trimmed = trace.trim();
        if (trimmed.length() < 3) {
            throw new IllegalArgumentException("Route length has to be at least 3 symbols, but: " + trimmed);
        }

        char fromChr = trimmed.charAt(0);
        if (!Character.isLetter(fromChr)) {
            throw new IllegalArgumentException("Microservice name has to be letter, but: " + fromChr);
        }

        char toChr = trimmed.charAt(1);
        if (!Character.isLetter(toChr)) {
            throw new IllegalArgumentException("Microservice name has to be letter, but: " + toChr);
        }

        int fromIdx = toNodeIdx(fromChr);
        int toIdx = toNodeIdx(toChr);

        int weight;
        try {
            weight = Integer.parseInt(trimmed.substring(2));
            if (weight < 0) {
                throw new IllegalArgumentException("Weight has to be non negative, but: " + weight);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Weight has to be integer, but: " + trimmed.substring(2));
        }

        return Edge.of(fromIdx, toIdx, weight);
    }

    /**
     * Convert indexed char to String representation
     */
    public static String toNodeName(int charNum) {
        return String.valueOf((char) charNum);
    }

    /**
     * Convert character to index
     */
    public static int toNodeIdx(Character character) {
        return (int) character;
    }

    public Edge findEdge(int fromIdx, int toIdx) {
        assertName(fromIdx);
        return matrix.get(fromIdx).get(toIdx);
    }

    private void assertName(int fromIdx) {
        if (!matrix.containsKey(fromIdx)) {
            throw new IndexOutOfBoundsException("Microservice " + toNodeName(fromIdx) + " not exists");
        }
    }

    public List<Edge> edges(int fromIdx) {
        assertName(fromIdx);
        return new ArrayList<>(matrix.get(fromIdx).values());
    }

    public int size() {
        return matrix.size();
    }
}
package distributed.tracing.strategies;

import java.util.ArrayList;
import java.util.List;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

import static distributed.tracing.Graph.toNodeName;

public class ShortestTraceCalculator {

    private final Graph graph;
    private final AverageLatencyCalculator averageLatencyCalculator;

    public ShortestTraceCalculator(String route) {
        this(new Graph(route));
    }

    public ShortestTraceCalculator(Graph graph) {
        this.averageLatencyCalculator = new AverageLatencyCalculator(graph);
        this.graph = graph;
    }

    /**
     * The length of the shortest trace (in terms of latency) between {@param fromChr} and {@param toChr}
     *
     * @param fromChr start Microservice
     * @param toChr   end Microservice
     * @return length of the shortest trace or {@literal -1} if latency for founded trace has not been found
     */
    public int calculate(char fromChr, char toChr) {
        return new ShortestTraceCalculatorInternal(fromChr, toChr).calculate();
    }

    public class ShortestTraceCalculatorInternal {
        private final List<String> allPath;
        private final char fromChr;
        private final int toIdx;

        public ShortestTraceCalculatorInternal(char fromChr, char toChr) {
            this.fromChr = fromChr;
            this.toIdx = Graph.toNodeIdx(toChr);
            this.allPath = new ArrayList<>();
        }

        public int calculate() {
            int from = Graph.toNodeIdx(fromChr);
            calculatePath(from, "");

            int minLatency = Integer.MAX_VALUE;
            int crnAvgLatency;
            for (String path : allPath) {
                crnAvgLatency = averageLatencyCalculator.calcAvgLatency(path);
                if (minLatency > crnAvgLatency) {
                    minLatency = crnAvgLatency;
                }
            }

            if (minLatency == Integer.MAX_VALUE) {
                return 0;
            }

            return minLatency;
        }

        private void calculatePath(int from, String path) {
            Edge toEdge = graph.findEdge(from, this.toIdx);
            if (toEdge != null) {
                allPath.add(toNodeName(fromChr) + path + Graph.toNodeName(this.toIdx));
                return;
            }

            for (Edge edge : graph.edges(from)) {
                String toNodeName = Graph.toNodeName(edge.toIdx);
                if (!path.isEmpty() && path.contains(toNodeName)) {
                    continue;
                }

                String next = path + toNodeName;
                if (path.length() < graph.limit()) {
                    calculatePath(edge.toIdx, next);
                } else {
                    //throw new IllegalStateException("Perhaps an infinite loop detected for path: " + next);
                }
            }
        }
    }
}
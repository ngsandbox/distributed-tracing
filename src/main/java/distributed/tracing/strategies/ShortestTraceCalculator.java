package distributed.tracing.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

import static distributed.tracing.Graph.toNodeName;
import static distributed.tracing.strategies.AverageLatencyCalculator.TRACE_NOT_FOUNT;

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
     * @return length of the shortest trace or {@literal -1} if traces has not been found
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

            return allPath.stream()
                    .map(averageLatencyCalculator::calcAvgLatency)
                    .filter(Optional::isPresent)
                    .mapToInt(Optional::get)
                    .min().orElse(TRACE_NOT_FOUNT);
        }

        private void calculatePath(int from, String path) {
            Edge toEdge = graph.findEdge(from, this.toIdx);
            if (toEdge != null) {
                allPath.add(toNodeName(fromChr) + path + toNodeName(this.toIdx));
                return;
            }

            for (Edge edge : graph.edges(from)) {
                if (path.length() < graph.limit()) {
                    calculatePath(edge.toIdx, path + toNodeName(edge.toIdx));
                } else {
                    //throw new IllegalStateException("Perhaps an infinite loop detected for path: " + next);
                }
            }
        }
    }
}

package distributed.tracing.strategies;

import java.util.Optional;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

import static distributed.tracing.strategies.AverageLatencyCalculator.NO_SUCH_TRACE;

public class ShortestTraceCalculator {

    private final Graph graph;

    public ShortestTraceCalculator(String route) {
        this(new Graph(route));
    }

    public ShortestTraceCalculator(Graph graph) {
        this.graph = graph;
    }

    /**
     * The length of the shortest trace (in terms of latency) between {@param fromChr} and {@param toChr}
     *
     * @param fromChr start Microservice
     * @param toChr   end Microservice
     * @return length of the shortest trace or {@literal NO SUCH TRACE} if traces has not been found
     */
    public String calculateStr(char fromChr, char toChr) {
        return new ShortestTraceCalculatorInternal(fromChr, toChr)
                .calculate().map(Object::toString)
                .orElse(NO_SUCH_TRACE);
    }

    public class ShortestTraceCalculatorInternal {
        private final char fromChr;
        private final int toIdx;
        private int minTrace;

        public ShortestTraceCalculatorInternal(char fromChr, char toChr) {
            this.fromChr = fromChr;
            this.toIdx = Graph.toNodeIdx(toChr);
            this.minTrace = Integer.MAX_VALUE;
        }

        public Optional<Integer> calculate() {
            int from = Graph.toNodeIdx(fromChr);
            calculatePath(from, 0, 0);
            return Optional.of(minTrace)
                    .filter(t -> t != Integer.MAX_VALUE);
        }

        private void calculatePath(int from, int sumWeight, int iterations) {
            Edge toEdge = graph.findEdge(from, this.toIdx);
            if (toEdge != null) {
                minTrace = Math.min(minTrace, sumWeight + toEdge.weight);
                return;
            }

            for (Edge edge : graph.edges(from)) {
                if (iterations < graph.limit()) {
                    calculatePath(edge.toIdx, sumWeight + edge.weight, ++iterations);
                } else {
                    //throw new IllegalStateException("Perhaps an infinite loop detected for path: " + next);
                }
            }
        }
    }
}

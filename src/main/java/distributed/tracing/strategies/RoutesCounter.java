package distributed.tracing.strategies;

import java.util.List;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class RoutesCounter {

    private final Graph graph;
    private final AverageLatencyCalculator latencyCalculator;

    public RoutesCounter(String route) {
        this(new Graph(route));
    }

    public RoutesCounter(Graph graph) {
        this.graph = graph;
        this.latencyCalculator = new AverageLatencyCalculator(graph);
    }

    /**
     * Count number of different traces from {@param fromChr} to {@param toChr}
     * with an average latency of less than {@param maxDistance}
     *
     * @return number of different traces
     */
    public int count(Character fromChr,
                     Character toChr,
                     int maxDistance) {
        return new RoutesCounterInternal(fromChr, toChr, maxDistance).count();
    }

    private class RoutesCounterInternal {
        private final Character fromChr;
        private final int fromIdx;
        private final int toIdx;
        private final int maxDistance;
        private int routesCount;

        private RoutesCounterInternal(Character fromChr,
                                      Character toChr,
                                      int maxDistance) {
            this.fromChr = fromChr;
            this.fromIdx = Graph.toNodeIdx(fromChr);
            this.toIdx = Graph.toNodeIdx(toChr);
            this.maxDistance = maxDistance;
            this.routesCount = 0;

        }

        public int count() {
            count(fromIdx, String.valueOf(fromChr));
            return routesCount;
        }

        private void count(int from, String path) {
            List<Edge> edges = graph.edges(from);
            for (Edge edge : edges) {
                String next = path + Graph.toNodeName(edge.toIdx);
                int distance = latencyCalculator.calcAvgLatency(next);
                if (this.toIdx == edge.toIdx
                        && distance < maxDistance) {
                    routesCount++;
                }

                if (distance < maxDistance) {
                    count(edge.toIdx, next);
                }
            }
        }

    }

}

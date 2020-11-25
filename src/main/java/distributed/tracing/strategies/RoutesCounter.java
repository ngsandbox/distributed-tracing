package distributed.tracing.strategies;

import java.util.List;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class RoutesCounter {

    private final Graph graph;

    public RoutesCounter(String route) {
        this(new Graph(route));
    }

    public RoutesCounter(Graph graph) {
        this.graph = graph;
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
        private final int fromIdx;
        private final int toIdx;
        private final int maxDistance;
        private int routesCount;

        private RoutesCounterInternal(Character fromChr,
                                      Character toChr,
                                      int maxDistance) {
            this.fromIdx = Graph.toNodeIdx(fromChr);
            this.toIdx = Graph.toNodeIdx(toChr);
            this.maxDistance = maxDistance;
            this.routesCount = 0;

        }

        public int count() {
            count(fromIdx, 0);
            return routesCount;
        }

        private void count(int from, int distance) {
            List<Edge> edges = graph.edges(from);
            for (Edge edge : edges) {
                int nextDistance = distance + edge.weight;
                if (this.toIdx == edge.toIdx
                        && nextDistance < maxDistance) {
                    routesCount++;
                }

                if (nextDistance < maxDistance) {
                    count(edge.toIdx, nextDistance);
                }
            }
        }

    }

}

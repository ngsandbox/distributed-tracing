package distributed.tracing.strategies;

import java.util.List;
import java.util.function.Predicate;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class TracesCounter {

    private final Graph graph;

    public TracesCounter(String route) {
        this(new Graph(route));
    }

    public TracesCounter(Graph graph) {
        this.graph = graph;
    }

    /**
     * Count number of traces originating in service {@param fromChr} and ending in {@param toChr}.
     *
     * @param fromChr   start character
     * @param toChr     end character
     * @param predicate control amount of hops
     * @return count of hops
     */
    public int count(Character fromChr,
                     Character toChr,
                     Predicate<Integer> predicate) {
        return new TracesCounterInternal(fromChr, toChr, predicate).count();
    }

    private class TracesCounterInternal {
        private final Character fromChr;
        private final Predicate<Integer> predicate;
        private final int toIdx;
        private int counter;

        public TracesCounterInternal(
                Character fromChr,
                Character toChr,
                Predicate<Integer> predicate) {
            this.fromChr = fromChr;
            this.predicate = predicate;
            this.toIdx = Graph.toNodeIdx(toChr);
            counter = 0;
        }

        public int count() {
            int fromIdx = Graph.toNodeIdx(fromChr);
            calculateTripsCount(fromIdx, String.valueOf(fromChr));
            return counter;
        }

        private void calculateTripsCount(int fromIdx,
                                         String path) {
            List<Edge> edges = graph.edges(fromIdx);
            for (Edge edge : edges) {
                String next = path + Graph.toNodeName(edge.toIdx);
                int pathLength = next.length() - 1;
                if (this.toIdx == edge.toIdx
                        && predicate.test(pathLength)) {
                    counter++;
                }

                if (pathLength <= graph.size() * 2) {
                    calculateTripsCount(edge.toIdx, next);
                }
            }
        }
    }
}

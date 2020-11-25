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
     * @param fromChr       start Microservice
     * @param toChr         end Microservice
     * @param hopsPredicate control amount of hops
     * @return count of hops
     */
    public int count(char fromChr,
                     char toChr,
                     Predicate<Integer> hopsPredicate) {
        return new TracesCounterInternal(fromChr, toChr, hopsPredicate).count();
    }

    private class TracesCounterInternal {
        private final char fromChr;
        private final Predicate<Integer> hopsPredicate;
        private final int toIdx;
        private int counter;

        public TracesCounterInternal(
                char fromChr,
                char toChr,
                Predicate<Integer> hopsPredicate) {
            this.fromChr = fromChr;
            this.hopsPredicate = hopsPredicate;
            this.toIdx = Graph.toNodeIdx(toChr);
            counter = 0;
        }

        public int count() {
            countTraces(Graph.toNodeIdx(fromChr), Graph.toNodeName(fromChr));
            return counter;
        }

        private void countTraces(int fromIdx,
                                 String path) {
            List<Edge> edges = graph.edges(fromIdx);
            for (Edge edge : edges) {
                String next = path + Graph.toNodeName(edge.toIdx);
                int pathLength = next.length() - 1;
                if (this.toIdx == edge.toIdx
                        && hopsPredicate.test(pathLength)) {
                    counter++;
                }

                if (pathLength < graph.limit()) {
                    countTraces(edge.toIdx, next);
                } else {
                    //throw new IllegalStateException("Perhaps an infinite loop detected for path: " + next);
                }
            }
        }
    }
}

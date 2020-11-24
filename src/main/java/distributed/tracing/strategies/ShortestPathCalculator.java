package distributed.tracing.strategies;

import java.util.ArrayList;
import java.util.List;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class ShortestPathCalculator {

    private final Graph graph;
    private final AverageLatencyCalculator averageLatencyCalculator;

    public ShortestPathCalculator(String route) {
        this(new Graph(route));
    }

    public ShortestPathCalculator(Graph graph) {
        this.averageLatencyCalculator = new AverageLatencyCalculator(graph);
        this.graph = graph;
    }

    public int calculate(Character fromChr, Character toChr) {
        return new ShortestPathCalculatorInternal(fromChr, toChr).calculate();
    }

    public class ShortestPathCalculatorInternal {
        private final List<String> allPath;
        private final Character fromChr;
        private final int toIdx;

        public ShortestPathCalculatorInternal(Character fromChr, Character toChr) {
            this.fromChr = fromChr;
            this.toIdx = Graph.toNodeIdx(toChr);
            this.allPath = new ArrayList<>();
        }

        public int calculate() {
            int from = Graph.toNodeIdx(fromChr);
            calculate(from, String.valueOf(fromChr));

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

        private void calculate(int from, String path) {
            List<Edge> edges = graph.edges(from);
            for (Edge edge : edges) {
                String toNodeName = Graph.toNodeName(edge.toIdx);
                if (path.length() > 1 && path.substring(1).contains(toNodeName)) {
                    continue;
                }

                String next = path + toNodeName;
                if (this.toIdx == edge.toIdx) {
                    allPath.add(next);
                }

                calculate(edge.toIdx, next);
            }
        }
    }
}

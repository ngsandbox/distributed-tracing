package distributed.tracing.strategies;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class AverageLatencyCalculator {

    private final Graph graph;

    public AverageLatencyCalculator(String route) {
        this(new Graph(route));
    }

    public AverageLatencyCalculator(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    /**
     * Calculate average latency of the trace {@param trace}
     *
     * @return numeric value of the calculated latency
     * or {@literal NO SUCH TRACE} if trace route has not been found
     */
    public String calculate(String trace) {
        int result = calcAvgLatency(trace);
        return result > -1 ? Integer.toString(result) : "NO SUCH TRACE";
    }

    /**
     * Calculate average latency of the trace {@param trace}
     *
     * @return numeric positive value of the calculated latency
     * or {@literal -1} if trace route has not been found
     * @throws IllegalArgumentException if trace is empty or specified microservice name is not exist
     */
    public int calcAvgLatency(String trace) {
        if (trace == null || trace.isEmpty()) {
            throw new IllegalArgumentException("Trace could not be empty");
        }

        int distance = 0;
        int fromIdx, toIdx;

        char[] chars = trace.trim().toCharArray();
        for (int i = 0; i < chars.length - 1; ) {
            fromIdx = Graph.toNodeIdx(chars[i++]);
            toIdx = Graph.toNodeIdx(chars[i]);
            Edge toEdge = graph.findEdge(fromIdx, toIdx);
            if (toEdge != null) {
                distance += toEdge.weight;
            } else {
                return -1;
            }
        }

        return distance;
    }
}

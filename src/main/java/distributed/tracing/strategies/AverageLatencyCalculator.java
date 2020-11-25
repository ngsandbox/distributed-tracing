package distributed.tracing.strategies;

import java.util.OptionalInt;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class AverageLatencyCalculator {
    public static String NO_SUCH_TRACE = "NO SUCH TRACE";

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
        OptionalInt result = calcAvgLatency(trace);
        if (result.isPresent()) {
            return Integer.toString(result.getAsInt());
        }

        return NO_SUCH_TRACE;
    }

    /**
     * Calculate average latency of the trace {@param trace}
     *
     * @return numeric positive value of the calculated latency
     * or {@literal empty} if trace route has not been found
     * @throws IllegalArgumentException if trace is empty or specified microservice name is not exist
     */
    public OptionalInt calcAvgLatency(String trace) {
        if (trace == null) {
            return OptionalInt.empty();
        }

        char[] chars = trace.trim().toCharArray();
        if (chars.length == 0) {
            return OptionalInt.empty();
        }

        boolean found = false;
        int distance = 0;
        for (int i = 0; i < chars.length - 1; ) {
            int fromIdx = Graph.toNodeIdx(chars[i++]);
            int toIdx = Graph.toNodeIdx(chars[i]);
            Edge toEdge = graph.findEdge(fromIdx, toIdx);
            if (toEdge != null) {
                found = true;
                distance += toEdge.weight;
            } else {
                return OptionalInt.empty();
            }
        }

        if (!found) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(distance);
    }
}

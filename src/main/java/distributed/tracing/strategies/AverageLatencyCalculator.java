package distributed.tracing.strategies;

import java.util.Optional;

import distributed.tracing.Edge;
import distributed.tracing.Graph;

public class AverageLatencyCalculator {

    public static int TRACE_NOT_FOUNT = -1;

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
        return calcAvgLatency(trace).map(Object::toString).orElse("NO SUCH TRACE");
    }

    /**
     * Calculate average latency of the trace {@param trace}
     *
     * @return numeric positive value of the calculated latency
     * or {@literal empty} if trace route has not been found
     * @throws IllegalArgumentException if trace is empty or specified microservice name is not exist
     */
    public Optional<Integer> calcAvgLatency(String trace) {
        if (trace == null) {
            return Optional.empty();
        }

        int distance = 0;
        int fromIdx, toIdx;

        char[] chars = trace.trim().toCharArray();
        if (chars.length == 0) {
            return Optional.empty();
        }

        for (int i = 0; i < chars.length - 1; ) {
            fromIdx = Graph.toNodeIdx(chars[i++]);
            toIdx = Graph.toNodeIdx(chars[i]);
            Edge toEdge = graph.findEdge(fromIdx, toIdx);
            if (toEdge != null) {
                distance += toEdge.weight;
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(distance);
    }
}

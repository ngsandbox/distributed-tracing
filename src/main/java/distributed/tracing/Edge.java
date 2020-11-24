package distributed.tracing;

import java.util.Objects;

public class Edge {
    public final int fromIdx;
    public final int toIdx;
    public final int weight;

    private Edge(int fromIdx,
                 int toIdx,
                 int weight) {
        this.fromIdx = fromIdx;
        this.toIdx = toIdx;
        this.weight = weight;
    }

    public static Edge of(int fromIdx,
                          int toIdx,
                          int weight) {
        return new Edge(fromIdx, toIdx, weight);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "fromIdx=" + Graph.toNodeName(fromIdx) +
                ", toIdx=" + Graph.toNodeName(toIdx) +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(toIdx);
    }
}

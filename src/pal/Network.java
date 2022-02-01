package pal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Network implements Comparable<Network> {
    List<Node> nodes = new ArrayList<>();
//    List<List<Node>> adjacentDegrees;
    List<Integer> sortedDegrees;
    int fast_servers = 0;
    int totalCost = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network pack = (Network) o;
        return Objects.equals(nodes, pack.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }

    @Override
    public int compareTo(Network o) {
        if (this.fast_servers < o.fast_servers)
            return 1;
        if (this.fast_servers > o.fast_servers)
            return -1;

        return 0;
    }
}

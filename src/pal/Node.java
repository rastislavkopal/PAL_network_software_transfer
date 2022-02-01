package pal;

import java.util.*;

public class Node {

    int id;
    List<Node> adjacentNodes = new ArrayList<>();
    int cost;
    boolean isFast = false;
    int nodeDegree = 0;

    public Node(int id) {
        this.id = id;
    }


    public Node(int id, int cost) {
        this.id = id;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "("
                + id +
                ')';
    }
}

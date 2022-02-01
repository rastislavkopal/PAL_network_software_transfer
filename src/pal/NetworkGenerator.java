package pal;

import java.util.*;

public class NetworkGenerator {

    Node[] newGraph;
    int old_n_connections, old_n_servers;
    int[] set;
    PriorityQueue<Network> possibleNetworks = new PriorityQueue<>();
    Map<Integer,List<Node>> newGraphEdges;
    Network oldNetwork;
//    int max_fast_servers;
//    List<Network> fastestNetworks = new ArrayList<>();

    public NetworkGenerator(Map<Integer,List<Node>> newGraphEdges, int[] set, Node[] newGraph, int old_n_connections, int old_n_servers, Network oldNetwork) {
        this.set = set;
        this.newGraph = newGraph;
        this.old_n_connections = old_n_connections;
        this.old_n_servers = old_n_servers;
        this.oldNetwork = oldNetwork;
        this.newGraphEdges = newGraphEdges;
    }

    public final boolean isConnected(Network network) {
        Deque<Node> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        queue.offerFirst(network.nodes.get(0));

        List<Integer> degreesList = new ArrayList<>();

        while (!queue.isEmpty()) {
            Node u = queue.removeLast();
            if (!visited.contains(u.id)) {
                if (u.isFast)
                    network.fast_servers++;

                visited.add(u.id);
                u.nodeDegree = 0;

                for (Node adj : u.adjacentNodes) {
                    if (network.nodes.contains(adj)) {
                        u.nodeDegree++;
                    }
                    if (!visited.contains(adj.id) && network.nodes.contains(adj)) {
//                        network.totalCost += newGraphEdges.get(adj.id)
                        queue.offerFirst(adj);
                    }
                }
                degreesList.add(u.nodeDegree);
            }
        }
        network.sortedDegrees = degreesList;
        return visited.size() == old_n_servers;
    }


    private final int countDegreeForNodeInPack(Node n, Network net) {
        int count = 0;

        for (Node i : n.adjacentNodes) {
            if (net.nodes.contains(i))
                count++;
        }
        return count;
    }

    // update pack's node degrees array -> sorted
    // map to each node pack.id -> list adjacent node degrees
    final void  updateNetworkNodeDegrees(Network network) {
        int[] degreesArray = new int[network.nodes.size()];
        List<Integer> degreesList = new ArrayList<>();


        for (Node n : network.nodes) {
            degreesList.add(n.nodeDegree);
        }

        Collections.sort(degreesList);
        network.sortedDegrees = degreesList;
    }

    private int current_max_fast = 0;

    public final void createNetwork(int[] subset) {
        int max_in_subset = 0;
        for (int i = 0; i < subset.length; i++) {
            if (newGraph[i].isFast)
                max_in_subset++;
        }
        if (max_in_subset > current_max_fast) {
            current_max_fast = max_in_subset;
        } else if (max_in_subset < current_max_fast) {
            return; // throw away if it would have less fast_servers
        }

        Network network = new Network();

        for (int i : subset) {
            network.nodes.add(newGraph[i]);
        }

        if (!isConnected(network))
            return;

//        updateNetworkNodeDegrees(network);
        Collections.sort(network.sortedDegrees);

        if (!network.sortedDegrees.equals(oldNetwork.sortedDegrees))
            return;

        possibleNetworks.add(network);
    }


    //       3. -------------------------------------------------------------------------
//       Idea:
//       Collect the items of the subset in a single result list.
//       Add one item to the result on each recursion level.
//       The technical advantage of the idea is that no
//       intermediate lists (results) are generated.

    public final void k_subsets(int k, int i_start, int[] result, int depth){
        if (depth == k){
            createNetwork(result);
            return;
        }

        int i_lastStart = set.length - (k-depth);
        for (int i = i_start; i < i_lastStart+1; i++) {
            result[depth] = set[i];
            k_subsets(k, i+1, result, depth+1);
        }
    }
}
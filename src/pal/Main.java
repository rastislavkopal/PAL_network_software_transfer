package pal;

import java.io.IOException;
import java.util.*;

public class Main {

    static int old_n_servers, old_n_connections, new_n_servers, new_n_connections, n_fast_servers;
    static Node[] oldGraph;
    static Node[] newGraph;
    static List<Integer> fastServers = new ArrayList<>();
    static Map<Integer, List<Node>> newGraphEdges = new HashMap<>();
    static NetworkGenerator generator;
    static Network oldNetwork;

    public static final void read() throws IOException {
        InputReader r = new InputReader();
        old_n_servers = r.nextInt();
        old_n_connections = r.nextInt();

        oldGraph = new Node[old_n_servers];
        for (int i = 0; i < old_n_servers; i++)
            oldGraph[i] = new Node(i);

        for (int i = 0; i < old_n_connections; i++) {
            int start = r.nextInt();
            int end = r.nextInt();
            oldGraph[start].adjacentNodes.add(oldGraph[end]);
            oldGraph[end].adjacentNodes.add(oldGraph[start]);
        }

        new_n_servers = r.nextInt();
        new_n_connections = r.nextInt();
        n_fast_servers  = r.nextInt();

        for (int i = 0; i < n_fast_servers; i++) {
            int fast = r.nextInt();
            fastServers.add(fast);
        }

        newGraph = new Node[new_n_servers];
        for (int i = 0; i < new_n_servers; i++) {
            newGraph[i] = new Node(i);
            if (fastServers.contains(i))
                newGraph[i].isFast = true;
            newGraphEdges.put(i, new ArrayList<>());
        }

        for (int i = 0; i < new_n_connections; i++) {
            int start = r.nextInt();
            int end = r.nextInt();
            int cost = r.nextInt();
            newGraph[start].adjacentNodes.add(newGraph[end]);
            newGraph[end].adjacentNodes.add(newGraph[start]);

            newGraphEdges.get(start).add(new Node(end, cost));
            newGraphEdges.get(end).add(new Node(start, cost));
        }

    }

    private static final int countDegreeForNodeInNetwork(Node n, Network p) {
        int count = 0;

        for (Node i : n.adjacentNodes) {
            if (p.nodes.contains(i))
                count++;
        }
        return count;
    }


    // update pack's node degrees array -> sorted
    // map to each node pack.id -> list adjacent node degrees
    static final void updateNetworkNodeDegrees(Network network) {
        int[] degreesArray = new int[network.nodes.size()];
        List<Integer> degreesList = new ArrayList<>();

        for(int i =0; i < network.nodes.size(); i++ ){
            Node current = network.nodes.get(i);
            int x = countDegreeForNodeInNetwork(current,network);
//            degreesArray[i] =
            degreesList.add(x);
        }

//        Arrays.sort(degreesArray);
        Collections.sort(degreesList);
        network.sortedDegrees = degreesList;
    }

    public static final void generateOldNetwork(){
        oldNetwork = new Network();
        for (int i = 0; i < oldGraph.length; i++){
            oldNetwork.nodes.add(oldGraph[i]);
        }
        updateNetworkNodeDegrees(oldNetwork);
    }

    public static final void updateCost(Network network) {
        Set<Integer> visited = new HashSet<>();
        for (Node n : network.nodes) {
            visited.add(n.id);
            for (Node adj : newGraphEdges.get(n.id)) {
                if (network.nodes.contains(adj) && !visited.contains(adj.id)) {
                    network.totalCost += adj.cost;
                }
            }
        }
    }

//    public static final Network updateMax2() {
//        Network minCostNetwork = null;
//        int minCost = Integer.MAX_VALUE;
//        for(Network n : generator.fastestNetworks){
//            updateCost(n);
//            if (minCost > n.totalCost) {
//                minCost = n.totalCost;
//                minCostNetwork = n;
//            }
//        }
////        System.out.println(Arrays.toString(minCostNetwork.nodes.toArray()) + " -->" + minCostNetwork.totalCost);
//        return minCostNetwork;
//    }

    public static final Network updateMaxPriority() {
        Network minCostNetwork = null;
        int max_fastest = generator.possibleNetworks.peek().fast_servers;
        int minCost = Integer.MAX_VALUE;

        while (true) {
            Network n = generator.possibleNetworks.poll();
            if (n == null || n.fast_servers != max_fastest)
                break;
            updateCost(n);
            if (minCost > n.totalCost) {
                minCost = n.totalCost;
                minCostNetwork = n;
            }
        }
//        System.out.println(Arrays.toString(minCostNetwork.nodes.toArray()) + " -->" + minCostNetwork.totalCost);
        return minCostNetwork;
    }


    public static void main(String[] args) throws IOException {
        read();

        int[] set = new int[new_n_servers];
        for (int i = 0; i < new_n_servers; i++) {
            set[i] = i;
        }

        generateOldNetwork();

        generator = new NetworkGenerator(newGraphEdges, set, newGraph, old_n_connections, old_n_servers, oldNetwork);
        generator.k_subsets(old_n_servers, 0, new int[old_n_servers],0);

        Network minCostNet = updateMaxPriority();

        System.out.println(minCostNet.fast_servers + " " + minCostNet.totalCost);
    }
}
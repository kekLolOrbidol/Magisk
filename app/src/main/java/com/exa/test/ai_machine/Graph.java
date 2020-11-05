package com.exa.test.ai_machine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

class Graph extends DirectedGraph {

    Graph() {
        super();
    }

    void addEdge(Edge edge) {
        super.addEdge(edge);
        Edge reverse = new Edge(edge.getDestination(), edge.getSource());
        super.addEdge(reverse);
    }

    int nodeCount(String nodeGroup, String player) {
        int count = 0;
        Set<Node> nodes = getAllNodes();
        for (Node node : nodes) {
            if (Objects.equals(node.getNodeGroup(), nodeGroup) && Objects.equals(node.getPlayer(), player)) {
                count++;
            }
        }
        return count;
    } 

    Set<Edge> getAllBidirectionalLinks() {
        HashSet<Edge> bidirectionalLinkSet = new HashSet<>();
        for (Edge e : getAllEdges()) {
            if (! bidirectionalLinkSet.contains(e) && ! bidirectionalLinkSet.contains(e.reverse())) {
                bidirectionalLinkSet.add(e); 
            }
        }
        return bidirectionalLinkSet;
    }
    
    ArrayList<Node> shortestPath(Node start, Node end, String player) {
        ArrayList<Node> initPath = new ArrayList<>();
        if (! (Objects.equals(start.getPlayer(), player) && Objects.equals(end.getPlayer(), player))) return initPath;
        initPath.add(start);
        LinkedList<ArrayList<Node>> pathQueue = new LinkedList<ArrayList<Node>>();
        pathQueue.add(initPath);
        while (pathQueue.size() > 0) {
            ArrayList<Node> newPathTrunk = pathQueue.removeFirst();
            Node lastNode = newPathTrunk.get(newPathTrunk.size() - 1);
            if (lastNode == end) {
                return newPathTrunk;
            }
            for (Node nextNode : childrenOf(lastNode)) {
                if (! Objects.equals(nextNode.getPlayer(), player)) continue;
                if (! newPathTrunk.contains(nextNode)) {
                    ArrayList<Node> newPath = new ArrayList<>();
                    newPath.addAll(newPathTrunk);
                    newPath.add(nextNode);
                    pathQueue.add(newPath);
                }
            }
        }
        return new ArrayList<>();
    }

}


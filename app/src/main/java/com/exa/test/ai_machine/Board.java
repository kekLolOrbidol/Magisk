package com.exa.test.ai_machine;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

class Board {

    Graph g;

    Board(String countryGraphString) {
        this.g = new Graph();
        Gson gson = new Gson();
        GsonTemplateCountryGraph countryGraphObject = gson.fromJson(countryGraphString, GsonTemplateCountryGraph.class);
        for (GsonTemplateContinent continent : countryGraphObject.continents) {
            for (GsonTemplateCountry country : continent.countries) {
                this.g.addNode(new Node(country.name, continent.name, country.player, country.count));
            }
        }
        for (GsonTemplateBidirectionalLink link : countryGraphObject.bidirectionalLinks) {
            this.g.addEdge(new Edge(this.g.getNode(link.fromCountry), this.g.getNode(link.toCountry)));
        }
    }

    static String getCountryListJSON(ArrayList<Node> nodes) {
        GsonTemplateCountryList countryListObject = new GsonTemplateCountryList();
        for (Node n : nodes) {
            countryListObject.countries.add(new GsonTemplateCountry(n.getName(), n.getPlayer(), n.getCount(), n.isModified()));
        }
        Gson gson = new Gson();
        return gson.toJson(countryListObject);
    }

    String getCountryGraphJSON() {
        GsonTemplateCountryGraph countryGraphObject = new GsonTemplateCountryGraph();
        for (String continentName : g.getAllNodeGroups()) {
            ArrayList<GsonTemplateCountry> countries = new ArrayList<>();
            for (Node n : g.getAllNodes(continentName)) {
                countries.add(new GsonTemplateCountry(n.getName(), n.getPlayer(), n.getCount(), n.isModified()));
            }
            countryGraphObject.continents.add(new GsonTemplateContinent(continentName, countries));
        }
        for (Edge e : g.getAllBidirectionalLinks()) {
            countryGraphObject.bidirectionalLinks.add(new GsonTemplateBidirectionalLink(e.getSource().getName(), e.getDestination().getName()));
        }
        Gson gson = new Gson();
        return gson.toJson(countryGraphObject);
    }

    String nodesSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes:\n\n");
        Set<Node> nodes = this.g.getAllNodes();
        List<Node> nodeList = new ArrayList<>(nodes);
        Collections.sort(nodeList);
        for (Node node : nodeList) {
            sb.append(node + "\n");
        }
        return sb.toString();
    }

    Node getNode(String name) {
        return this.g.getNode(name);
    }

    ArrayList<Node> shortestPath(Node from, Node to, String player) {
        return this.g.shortestPath(from, to, player);
    }

    void initialAllocationOfCountries(String playerA, String playerB) {
        // assuming number of nodes is even, each player must have equal number of countries
        Set<Node> nodes = this.g.getAllNodes();
        int nodeCount = nodes.size();
        // integer division (no remainder)
        int nodeCountPlayerA = nodeCount / 2;
        int nodeCountPlayerB = nodeCount - nodeCountPlayerA;
        ArrayList<String> draws = new ArrayList<>();
        for (int i = 0; i < nodeCountPlayerA; i++) {
            draws.add(playerA);
        }
        for (int i = 0; i < nodeCountPlayerB; i++) {
            draws.add(playerB);
        }
        Collections.shuffle(draws);
        for (Node node : nodes) {
            String player = draws.remove(0);
            node.setPlayer(player);
            node.setCount(1);
        }
    }

    void allocationOfExtraTroops(String player, int extraTroops) {
        Random randomGenerator = new Random();
        Set<Node> nodes = this.g.getAllNodes();
        ArrayList<Node> nodesPlayer = new ArrayList<>();
        for (Node node : nodes) {
            if (Objects.equals(node.getPlayer(), player)) {
                nodesPlayer.add(node);
            }
        }
        if (nodesPlayer.size() > 0) {
            for (int i = 0; i < extraTroops; i++) {
                int randomIndex = randomGenerator.nextInt(nodesPlayer.size());
                Node node = nodesPlayer.get(randomIndex);
                node.incrementCount();
            }
        }
    }

    void initialAllocationOfExtraTroops(int extraTroopsEach) {
        Set<String> players = this.g.getAllPlayers();
        for (String player : players) {
            allocationOfExtraTroops(player, extraTroopsEach);
        }
    }

    ArrayList<Node> possibleAttackingCountries(String attackingPlayer, Node targetCountry) {
        ArrayList<Node> possibleAttackingCountries = new ArrayList<>();
        if (Objects.equals(targetCountry.getPlayer(), attackingPlayer)) {
            return possibleAttackingCountries;
        }
        HashSet<Node> neighbors = this.g.childrenOf(targetCountry);
        for (Node neighbor : neighbors) {
            if (Objects.equals(neighbor.getPlayer(), attackingPlayer) && neighbor.getCount() > 1) {
                possibleAttackingCountries.add(neighbor);
            }
        }
        return possibleAttackingCountries;
    }

    ArrayList<Node> possibleTargetCountries(String attackingPlayer) {
        ArrayList<Node> enemyCountries = new ArrayList<>();
        Set<Node> nodes = this.g.getAllNodes();
        for (Node node : nodes) {
            if (!Objects.equals(node.getPlayer(), attackingPlayer)) {
                enemyCountries.add(node);
            }
        }
        ArrayList<Node> possibleTargetCountries = new ArrayList<>();
        for (Node enemyCountry : enemyCountries) {
            ArrayList<Node> possibleAttackingCountries = possibleAttackingCountries(attackingPlayer, enemyCountry);
            if (possibleAttackingCountries.size() > 0) {
                possibleTargetCountries.add(enemyCountry);
            }
        }
        return possibleTargetCountries;
    }

    ArrayList<Node> possibleTargetCountries(String attackingPlayer, Node attackingCountry) {
        ArrayList<Node> possibleTargetCountries = new ArrayList<>();
        if (!(Objects.equals(attackingCountry.getPlayer(), attackingPlayer) && attackingCountry.getCount() > 1)) {
            return possibleTargetCountries;
        }
        HashSet<Node> neighbors = this.g.childrenOf(attackingCountry);
        for (Node neighbor : neighbors) {
            if (!Objects.equals(neighbor.getPlayer(), attackingPlayer)) {
                possibleTargetCountries.add(neighbor);
            }
        }
        return possibleTargetCountries;
    }

    void battle(Node attackingCountry, Node targetCountry) {
        // choose winner by drawing from urn, with each player
        // being represented in proportion to the number of troops
        ArrayList<String> urn = new ArrayList<>();
        for (int i = 0; i < attackingCountry.getCount(); i++) {
            urn.add(attackingCountry.getPlayer());
        }
        for (int i = 0; i < targetCountry.getCount(); i++) {
            urn.add(targetCountry.getPlayer());
        }
        Collections.shuffle(urn);
        String winner = urn.remove(0);
        // simplification: always set both counts to 1
        attackingCountry.setCount(1);
        targetCountry.setCount(1);
        targetCountry.setPlayer(winner);
    }

    void attack(String attackingPlayer) {
        Random randomGenerator = new Random();
        ArrayList<Node> possibleTargetCountries = possibleTargetCountries(attackingPlayer);
        if (possibleTargetCountries.size() > 0) {
            int randomIndex = randomGenerator.nextInt(possibleTargetCountries.size());
            Node targetCountry = possibleTargetCountries.get(randomIndex);
            ArrayList<Node> possibleAttackingCountries = possibleAttackingCountries(attackingPlayer, targetCountry);
            if (possibleAttackingCountries.size() > 0) {
                randomIndex = randomGenerator.nextInt(possibleAttackingCountries.size());
                Node attackingCountry = possibleAttackingCountries.get(randomIndex);
                battle(attackingCountry, targetCountry);
            }
        }
    }

    ArrayList<Node> possibleSources(String player) {
        ArrayList<Node> possibleSources = new ArrayList<>();
        Set<Node> nodes = this.g.getAllNodes();
        for (Node node : nodes) {
            if (Objects.equals(node.getPlayer(), player) && node.getCount() > 1) {
                possibleSources.add(node);
            }
        }
        return possibleSources;
    }

    ArrayList<Node> possibleSources(String player, Node destination) {
        ArrayList<Node> possibleSources = new ArrayList<>();
        ArrayList<Node> candidates = possibleSources(player);
        for (Node candidate : candidates) {
            if (shortestPath(candidate, destination, player).size() > 0) {
                possibleSources.add(candidate);
            }
        }
        return possibleSources;
    }

    ArrayList<Node> possibleDestinations(String player) {
        ArrayList<Node> possibleDestinations = new ArrayList<>();
        Set<Node> nodes = this.g.getAllNodes();
        for (Node node : nodes) {
            if (Objects.equals(node.getPlayer(), player)) {
                possibleDestinations.add(node);
            }
        }
        return possibleDestinations;
    }

    ArrayList<Node> possibleDestinations(String player, Node source) {
        ArrayList<Node> possibleDestinations = new ArrayList<>();
        ArrayList<Node> candidates = possibleDestinations(player);
        for (Node candidate : candidates) {
            if (shortestPath(source, candidate, player).size() > 0) {
                possibleDestinations.add(candidate);
            }
        }
        return possibleDestinations;
    }

    void moveTroops(String player) {
        ArrayList<Node> possibleSources = possibleSources(player);
        ArrayList<Node> possibleDestinations = possibleDestinations(player);
        if (possibleSources.size() > 0 && possibleDestinations.size() > 0) {
            for (Node possibleSource : possibleSources) {
                for (Node possibleDestination : possibleDestinations) {
                    if (possibleDestination != possibleSource
                            && shortestPath(possibleSource, possibleDestination, player).size() > 0) {
                        possibleDestination.setCount(possibleDestination.getCount() + possibleSource.getCount() - 1);
                        possibleSource.setCount(1);
                        return;
                    }
                }
            }
        }
    }

    void nextMove(String player) {
        allocationOfExtraTroops(player, 2);
        attack(player);
        moveTroops(player);
    }

    void resetModified() {
        Set<Node> nodes = this.g.getAllNodes();
        for (Node node : nodes) {
            node.resetModified();
        }
    }

    boolean gameOver() {
        Set<String> nodeGroups = this.g.getAllNodeGroups();
        Set<String> players = this.g.getAllPlayers();
        for (String nodeGroup : nodeGroups) {
            for (String player : players) {
                if (this.g.nodeCount(nodeGroup, player) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
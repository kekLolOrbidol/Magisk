package com.exa.test.ai_machine;

import java.util.Objects;

class Node implements Comparable<Node> {

    private final String name; // precondition: must be unique
    private final String nodeGroup;
    private String player;
    private int count;
    private boolean modified;

    Node(String name, String nodeGroup, String player, int count) {
        this.name = name;
        this.nodeGroup = nodeGroup;
        this.player = player;
        this.count = count;
        this.modified = false;
    }

    Node(String name, String nodeGroup) {
        this(name, nodeGroup, "?", 0); 
    }

    Node(String name) {
        this(name, "?", "?", 0); 
    }

    String getName() {
        return this.name;
    }

    String getNodeGroup() {
        return this.nodeGroup;
    }

    String getPlayer() {
        return this.player;
    }

    int getCount() {
        return this.count;
    }

    boolean isModified() {
        return this.modified;
    }

    void setPlayer(String player) {
        this.player = player;
        this.modified = true;
    }

    void setCount(int count) {
        this.count = count;
        this.modified = true;
    }

    void resetModified() {
        this.modified = false;
    }

    void incrementCount() {
        this.count += 1;
        this.modified = true;
    }

    void decrementCount() {
        if (this.count > 0) {
            this.count -= 1;
            this.modified = true;
        }
    }

    public String toString() {
        return this.name + " in " + this.nodeGroup + " (" + this.player + ": " + this.count + ")"; 
    }

    public int hashCode() {
        return Objects.hash(this.name);
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (this.getClass() != otherObject.getClass()) return false;
        Node other = (Node) otherObject;
        return Objects.equals(this.name, other.name);
    }

    public int compareTo(Node other) {
        int i = this.nodeGroup.compareTo(other.nodeGroup);
        if (i != 0) {
            return i;
        }
        return this.name.compareTo(other.name);
    }

}


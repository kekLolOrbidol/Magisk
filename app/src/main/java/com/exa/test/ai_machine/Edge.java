package com.exa.test.ai_machine;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

class Edge implements Comparable<Edge> {
    
    private final Node source;
    private final Node destination;

    Edge(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    Node getSource() {
        return this.source;
    }

    Node getDestination() {
        return this.destination;
    }

    Edge reverse() {
        return new Edge(this.destination, this.source);
    }

    public String toString() {
        return this.source.getName() + " -> " + this.destination.getName();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int hashCode() {
        return Objects.hash(this.source, this.destination);
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (this.getClass() != otherObject.getClass()) return false;
        Edge other = (Edge) otherObject;
        return this.source == other.source && this.destination == other.destination;
    }

    public int compareTo(Edge other) {
        int i = this.source.compareTo(other.source);
        if (i != 0) {
            return i;
        }
        return this.destination.compareTo(other.destination);
    }

}


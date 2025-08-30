package com.example.immunobubby.pathfinding;

import java.util.Objects;

public class NodoAStar implements Comparable<NodoAStar> {
    private final Nodo nodo;
    private NodoAStar parent;
    private double g; // costo dal nodo iniziale
    private double h; // euristica
    private double f; // g + h

    public NodoAStar(Nodo nodo) {
        this.nodo = nodo;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public NodoAStar getParent() {
        return parent;
    }

    public void setParent(NodoAStar parent) {
        this.parent = parent;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
        aggiornaF();
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
        aggiornaF();
    }

    public double getF() {
        return f;
    }

    private void aggiornaF() {
        this.f = this.g + this.h;
    }

    @Override
    public int compareTo(NodoAStar o) {
        return Double.compare(this.f, o.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NodoAStar)) return false;
        NodoAStar other = (NodoAStar) obj;
        return nodo.equals(other.nodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodo);
    }
}

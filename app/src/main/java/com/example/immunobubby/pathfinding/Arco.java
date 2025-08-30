package com.example.immunobubby.pathfinding;

public class Arco {
    private final Nodo da;
    private final Nodo a;
    private final double costo;

    public Arco(Nodo da, Nodo a, double costo) {
        this.da = da;
        this.a = a;
        this.costo = costo;
    }

    public Nodo getDa() {
        return da;
    }

    public Nodo getA() {
        return a;
    }

    public double getCosto() {
        return costo;
    }
}

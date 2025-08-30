package com.example.immunobubby.pathfinding;

import android.util.Log;

import com.example.immunobubby.PollenManager;

import java.util.*;

public class AStarPathFinder {

    private static final String TAG = "AStarPathFinder";

    private final Map<Nodo, List<Arco>> grafo;
    private final PollenManager pollenManager;

    public AStarPathFinder(Map<Nodo, List<Arco>> grafo, PollenManager pollenManager) {
        this.grafo = grafo;
        this.pollenManager = pollenManager;
        Log.d(TAG, "PathFinder inizializzato con grafo e PollenManager");
    }

    // Wrapper per compatibilità con PercorsoMappaActivity
    public List<Nodo> calcolaPercorsoOttimale(Nodo start, Nodo goal) {
        Log.d(TAG, "Calcolo percorso ottimale da " + start + " a " + goal);
        List<Nodo> percorso = trovaPercorso(start, goal);
        Log.d(TAG, "Percorso calcolato: " + percorso.size() + " nodi");
        return percorso;
    }

    public List<Nodo> trovaPercorso(Nodo start, Nodo goal) {
        Log.d(TAG, "Inizio A* tra nodi start=" + start + " e goal=" + goal);

        PriorityQueue<NodoAStar> openSet = new PriorityQueue<>();
        Map<Nodo, NodoAStar> allNodes = new HashMap<>();
        Set<Nodo> closedSet = new HashSet<>();

        NodoAStar startNode = new NodoAStar(start);
        startNode.setG(0);
        startNode.setH(heuristic(start, goal));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            NodoAStar current = openSet.poll();
            Log.d(TAG, "Nodo corrente: " + current.getNodo() + ", G=" + current.getG() + ", H=" + current.getH());

            if (current.getNodo().equals(goal)) {
                Log.d(TAG, "Goal raggiunto!");
                return ricostruisciPercorso(current);
            }

            closedSet.add(current.getNodo());

            for (Arco arco : grafo.getOrDefault(current.getNodo(), Collections.emptyList())) {
                Nodo vicino = arco.getA();
                if (closedSet.contains(vicino)) continue;

                double tentativeG = current.getG() + arco.getCosto();

                // Penalità pollini (semplificata)
                int penalty = pollenManager.getPenalty(vicino.getLat(), vicino.getLon());
                Log.d(TAG, "Vicino: " + vicino + ", Costo arco=" + arco.getCosto() + ", Penalità polline=" + penalty);
                tentativeG += penalty;

                NodoAStar vicinoNode = allNodes.getOrDefault(vicino, new NodoAStar(vicino));

                if (tentativeG < vicinoNode.getG() || !allNodes.containsKey(vicino)) {
                    vicinoNode.setParent(current);
                    vicinoNode.setG(tentativeG);
                    vicinoNode.setH(heuristic(vicino, goal));

                    if (!openSet.contains(vicinoNode)) openSet.add(vicinoNode);
                    allNodes.put(vicino, vicinoNode);

                    Log.d(TAG, "Aggiornato nodo vicino: " + vicino + ", G=" + vicinoNode.getG() + ", H=" + vicinoNode.getH());
                }
            }
        }

        Log.w(TAG, "Nessun percorso trovato tra " + start + " e " + goal);
        return Collections.emptyList();
    }

    private double heuristic(Nodo a, Nodo b) {
        double dx = a.getLat() - b.getLat();
        double dy = a.getLon() - b.getLon();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<Nodo> ricostruisciPercorso(NodoAStar goalNode) {
        List<Nodo> percorso = new ArrayList<>();
        NodoAStar current = goalNode;
        while (current != null) {
            percorso.add(current.getNodo());
            current = current.getParent();
        }
        Collections.reverse(percorso);
        Log.d(TAG, "Percorso ricostruito: " + percorso);
        return percorso;
    }
}

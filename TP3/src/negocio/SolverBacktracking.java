package negocio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Algoritmo exacto por Backtracking para el conjunto dominante mínimo.
 * Explora todas las combinaciones posibles de vértices para encontrar
 * el conjunto dominante de menor tamaño.
 */
public class SolverBacktracking implements Solver {

    private Set<Integer> mejorConjunto;
    private List<Integer> verticesLista;

    @Override
    public Set<Integer> resolver(Grafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser null");
        }

        int n = grafo.cantidadVertices();
        if (n == 0) {
            return new TreeSet<>();
        }

        verticesLista = new ArrayList<>(grafo.vertices());
        mejorConjunto = null;

        // Comenzamos la recursión con un conjunto parcial vacío
        backtracking(0, new TreeSet<>(), grafo);

        return mejorConjunto != null ? mejorConjunto : new TreeSet<>();
    }

    private void backtracking(int index, Set<Integer> actual, Grafo grafo) {
        // Si ya encontramos un conjunto válido y el tamaño actual es mayor o igual
        // al mejor encontrado, podamos la rama (optimización).
        if (mejorConjunto != null && actual.size() >= mejorConjunto.size()) {
            return;
        }

        // Si ya recorrimos todos los vértices, verificamos si es dominante
        if (index == verticesLista.size()) {
            if (esDominante(grafo, actual)) {
                if (mejorConjunto == null || actual.size() < mejorConjunto.size()) {
                    mejorConjunto = new TreeSet<>(actual);
                }
            }
            return;
        }

        int v = verticesLista.get(index);

        // Opción 1: Incluir el vértice 'v' en el conjunto dominante
        actual.add(v);
        backtracking(index + 1, actual, grafo);
        actual.remove(v); // Backtrack

        // Opción 2: NO incluir el vértice 'v' en el conjunto dominante
        backtracking(index + 1, actual, grafo);
    }

    private boolean esDominante(Grafo g, Set<Integer> conjunto) {
        for (int v : g.vertices()) {
            if (conjunto.contains(v)) {
                continue;
            }
            boolean dominado = false;
            for (int vecino : g.vecinos(v)) {
                if (conjunto.contains(vecino)) {
                    dominado = true;
                    break;
                }
            }
            if (!dominado) {
                return false;
            }
        }
        return true;
    }
}
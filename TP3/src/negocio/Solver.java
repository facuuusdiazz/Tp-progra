package negocio;

import java.util.Set;

/**
 * Algoritmo que encuentra un conjunto dominante de un grafo.
 * Permite intercambiar la estrategia (goloso, backtracking, ...) sin que
 * el resto de la aplicación cambie.
 */
public interface Solver {

    /**
     * @return un conjunto dominante del grafo: todo vértice que no está en el
     *         conjunto tiene al menos un vecino en él
     * @throws IllegalArgumentException si el grafo es null
     */
    Set<Integer> resolver(Grafo grafo);
}

package negocio;

import java.util.Set;
import java.util.TreeSet;

/**
 * Algoritmo goloso para el conjunto dominante mínimo.
 *
 * En cada paso elige el vértice que domina a más vértices todavía no
 * dominados (contándose a sí mismo y a sus vecinos), hasta dominar todo el
 * grafo. Ante un empate gana el vértice de menor identificador, así el
 * resultado es siempre el mismo para un mismo grafo.
 *
 * No garantiza el mínimo (el problema es NP-completo), pero siempre devuelve
 * un conjunto dominante válido.
 *
 * Complejidad: a lo sumo n iteraciones, y cada una recorre todos los
 * vértices y sus vecinos, o sea O(n * (n + m)) salvo el factor logarítmico
 * de los conjuntos ordenados.
 */
public class SolverGoloso implements Solver {

    @Override
    public Set<Integer> resolver(Grafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser null");
        }

        Set<Integer> dominante = new TreeSet<>();
        Set<Integer> noDominados = new TreeSet<>(grafo.vertices());

        while (!noDominados.isEmpty()) {
            int elegido = elegirMejorVertice(grafo, noDominados);
            dominante.add(elegido);
            noDominados.remove(elegido);
            noDominados.removeAll(grafo.vecinos(elegido));
        }
        return dominante;
    }

    // Se consideran todos los vértices como candidatos, incluso los ya
    // dominados, porque pueden dominar a vecinos que todavía no lo están.
    private int elegirMejorVertice(Grafo grafo, Set<Integer> noDominados) {
        int mejor = -1;
        int mejorCobertura = -1;
        for (int v : grafo.vertices()) {
            int cobertura = cobertura(grafo, v, noDominados);
            if (cobertura > mejorCobertura) {
                mejor = v;
                mejorCobertura = cobertura;
            }
        }
        return mejor;
    }

    private int cobertura(Grafo grafo, int v, Set<Integer> noDominados) {
        int cobertura = noDominados.contains(v) ? 1 : 0;
        for (int vecino : grafo.vecinos(v)) {
            if (noDominados.contains(vecino)) {
                cobertura++;
            }
        }
        return cobertura;
    }
}

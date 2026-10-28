package negocio;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Grafo simple y no dirigido (sin bucles ni aristas repetidas).
 * Los vértices se identifican con enteros consecutivos que asigna el propio
 * grafo, empezando en 1.
 *
 * Representación: lista de adyacencia con un conjunto de vecinos por vértice.
 * - agregarVertice, agregarArista, existeArista: O(log n)
 * - vecinos(v): O(1) (devuelve una vista de solo lectura)
 * - recorrer los vecinos de v: O(grado(v))
 */
public class Grafo {

    private final Map<Integer, Set<Integer>> vecinos = new TreeMap<>();
    private int proximoId = 1;

    /** Agrega un vértice nuevo y devuelve su identificador. */
    public int agregarVertice() {
        int id = proximoId++;
        vecinos.put(id, new TreeSet<>());
        return id;
    }

    /**
     * Agrega la arista entre i y j. Si ya existía, no hace nada.
     *
     * @throws IllegalArgumentException si alguno de los extremos no existe
     *                                  o si i == j
     */
    public void agregarArista(int i, int j) {
        verificarVertice(i);
        verificarVertice(j);
        if (i == j) {
            throw new IllegalArgumentException("No se permiten aristas de un vértice consigo mismo: " + i);
        }
        vecinos.get(i).add(j);
        vecinos.get(j).add(i);
    }

    public boolean existeVertice(int v) {
        return vecinos.containsKey(v);
    }

    /** @throws IllegalArgumentException si alguno de los vértices no existe */
    public boolean existeArista(int i, int j) {
        verificarVertice(i);
        verificarVertice(j);
        return vecinos.get(i).contains(j);
    }

    /**
     * Vecinos de v, en orden creciente. La vista es de solo lectura.
     *
     * @throws IllegalArgumentException si v no existe
     */
    public Set<Integer> vecinos(int v) {
        verificarVertice(v);
        return Collections.unmodifiableSet(vecinos.get(v));
    }

    /** Todos los vértices, en orden creciente. La vista es de solo lectura. */
    public Set<Integer> vertices() {
        return Collections.unmodifiableSet(vecinos.keySet());
    }

    public int grado(int v) {
        return vecinos(v).size();
    }

    public int cantidadVertices() {
        return vecinos.size();
    }

    public int cantidadAristas() {
        int sumaGrados = 0;
        for (Set<Integer> vs : vecinos.values()) {
            sumaGrados += vs.size();
        }
        return sumaGrados / 2;
    }

    private void verificarVertice(int v) {
        if (!vecinos.containsKey(v)) {
            throw new IllegalArgumentException("El vértice " + v + " no existe");
        }
    }
}

package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import negocio.Grafo;
import negocio.Solver;
import persistencia.GrafoJSON;
import persistencia.SerializadorGrafo;

/**
 * Intermediario entre la interfaz y la lógica de negocio.
 * La interfaz solo conoce a esta clase; no conoce a Grafo ni al algoritmo
 * concreto. El Solver se recibe por constructor, así se puede cambiar el
 * algoritmo (goloso, backtracking, ...) sin tocar la interfaz.
 */
public class ControladorGrafo {

    private final Solver solver;
    private Grafo grafo;

    public ControladorGrafo(Solver solver) {
        if (solver == null) {
            throw new IllegalArgumentException("El solver no puede ser null");
        }
        this.solver = solver;
        this.grafo = new Grafo();
    }

    /** Agrega un vértice nuevo y devuelve su número. */
    public int agregarVertice() {
        return grafo.agregarVertice();
    }

    /** @throws IllegalArgumentException si algún extremo no existe o son el mismo vértice */
    public void agregarArista(int i, int j) {
        grafo.agregarArista(i, j);
    }

    public boolean existeArista(int i, int j) {
        return grafo.existeArista(i, j);
    }

    public Set<Integer> calcularConjuntoDominante() {
        return solver.resolver(grafo);
    }

    /** Vértices en orden creciente (vista de solo lectura). */
    public Set<Integer> vertices() {
        return grafo.vertices();
    }

    /** Cada arista una sola vez, como par {menor, mayor}. */
    public List<int[]> aristas() {
        List<int[]> aristas = new ArrayList<>();
        for (int v : grafo.vertices()) {
            for (int w : grafo.vecinos(v)) {
                if (v < w) {
                    aristas.add(new int[] { v, w });
                }
            }
        }
        return aristas;
    }

    public int cantidadVertices() {
        return grafo.cantidadVertices();
    }

    public int cantidadAristas() {
        return grafo.cantidadAristas();
    }

    /** Descarta el grafo actual y empieza uno vacío. */
    public void nuevoGrafo() {
        grafo = new Grafo();
    }

    // ---------- PERSISTENCIA JSON ----------

    /** Guarda el grafo actual en un archivo JSON */
    public void guardarGrafo(String ruta) throws Exception {
        GrafoJSON datos = new GrafoJSON();
        datos.cantidadVertices = grafo.cantidadVertices();
        datos.aristas = aristas();
        
        SerializadorGrafo.guardar(datos, ruta);
    }

    /** Reemplaza el grafo actual leyendo desde un archivo JSON */
    public void cargarGrafo(String ruta) throws Exception {
        GrafoJSON datos = SerializadorGrafo.cargar(ruta);
        
        Grafo nuevoGrafo = new Grafo();
        
        // 1. Agregamos los vértices (se numerarán automáticamente del 1 a N)
        for (int i = 0; i < datos.cantidadVertices; i++) {
            nuevoGrafo.agregarVertice();
        }
        
        // 2. Conectamos las aristas
        if (datos.aristas != null) {
            for (int[] arista : datos.aristas) {
                nuevoGrafo.agregarArista(arista[0], arista[1]);
            }
        }
        
        // 3. Reemplazamos el grafo en memoria
        this.grafo = nuevoGrafo;
    }
}
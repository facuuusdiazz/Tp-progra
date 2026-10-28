package negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SolverBacktrackingTest {

    private Solver solver;

    @Before
    public void setUp() {
        solver = new SolverBacktracking();
    }

    @Test
    public void grafoVacioTieneConjuntoDominanteVacio() {
        assertTrue(solver.resolver(new Grafo()).isEmpty());
    }

    @Test
    public void grafoDeLaFigura1EncuentraElMinimoExacto() {
        Grafo g = new Grafo();
        for (int i = 0; i < 6; i++) {
            g.agregarVertice();
        }
        int[][] aristas = { { 6, 4 }, { 4, 5 }, { 4, 3 }, { 5, 1 }, { 5, 2 }, { 1, 2 }, { 2, 3 } };
        for (int[] a : aristas) {
            g.agregarArista(a[0], a[1]);
        }

        Set<Integer> resultado = solver.resolver(g);
        
        // El tamaño del conjunto dominante mínimo para la figura 1 es 2 (ej: {2, 4})
        assertEquals(2, resultado.size());
        assertTrue(esDominante(g, resultado));
    }

    private boolean esDominante(Grafo g, Set<Integer> conjunto) {
        for (int v : g.vertices()) {
            if (conjunto.contains(v)) continue;
            boolean dominado = false;
            for (int vecino : g.vecinos(v)) {
                if (conjunto.contains(vecino)) dominado = true;
            }
            if (!dominado) return false;
        }
        return true;
    }
}
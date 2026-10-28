package negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SolverGolosoTest {

    private Solver solver;

    @Before
    public void setUp() {
        solver = new SolverGoloso();
    }

    @Test
    public void grafoVacioTieneConjuntoDominanteVacio() {
        assertTrue(solver.resolver(new Grafo()).isEmpty());
    }

    @Test
    public void grafoSinAristasNecesitaTodosLosVertices() {
        Grafo g = grafoConVertices(4);

        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4)), solver.resolver(g));
    }

    @Test
    public void grafoCompletoSeDominaConUnSoloVertice() {
        Grafo g = grafoConVertices(5);
        for (int i = 1; i <= 5; i++) {
            for (int j = i + 1; j <= 5; j++) {
                g.agregarArista(i, j);
            }
        }

        assertEquals(1, solver.resolver(g).size());
    }

    @Test
    public void verticeAisladoSiempreEstaEnElConjunto() {
        Grafo g = grafoConVertices(3);
        g.agregarArista(1, 2);

        Set<Integer> resultado = solver.resolver(g);

        assertTrue(resultado.contains(3));
        assertEquals(2, resultado.size());
        assertTrue(esDominante(g, resultado));
    }

    @Test
    public void grafoDeLaFigura1() {
        Grafo g = figura1();

        Set<Integer> resultado = solver.resolver(g);

        assertEquals(new HashSet<>(Arrays.asList(2, 4)), resultado);
        assertTrue(esDominante(g, resultado));
    }

    @Test
    public void caminoDeCincoVertices() {
        Grafo g = grafoConVertices(5);
        for (int i = 1; i < 5; i++) {
            g.agregarArista(i, i + 1);
        }

        Set<Integer> resultado = solver.resolver(g);

        assertTrue(esDominante(g, resultado));
        assertEquals(2, resultado.size());
    }

    @Test
    public void elResultadoEsSiempreDominanteEnGrafosAleatorios() {
        Random random = new Random(12345);
        for (int caso = 0; caso < 50; caso++) {
            int n = 1 + random.nextInt(20);
            Grafo g = grafoConVertices(n);
            for (int i = 1; i <= n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    if (random.nextBoolean()) {
                        g.agregarArista(i, j);
                    }
                }
            }

            assertTrue("Falló en el caso " + caso, esDominante(g, solver.resolver(g)));
        }
    }

    @Test
    public void resolverNoModificaElGrafo() {
        Grafo g = figura1();

        solver.resolver(g);

        assertEquals(6, g.cantidadVertices());
        assertEquals(7, g.cantidadAristas());
    }

    @Test
    public void elResultadoEsElMismoEnDosEjecuciones() {
        Grafo g = figura1();

        assertEquals(solver.resolver(g), solver.resolver(g));
    }

    @Test(expected = IllegalArgumentException.class)
    public void grafoNullLanzaExcepcion() {
        solver.resolver(null);
    }

    // ---------- utilidades ----------

    private Grafo grafoConVertices(int n) {
        Grafo g = new Grafo();
        for (int i = 0; i < n; i++) {
            g.agregarVertice();
        }
        return g;
    }

    private Grafo figura1() {
        Grafo g = grafoConVertices(6);
        g.agregarArista(6, 4);
        g.agregarArista(4, 5);
        g.agregarArista(4, 3);
        g.agregarArista(5, 1);
        g.agregarArista(5, 2);
        g.agregarArista(1, 2);
        g.agregarArista(2, 3);
        return g;
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
                }
            }
            if (!dominado) {
                return false;
            }
        }
        return true;
    }
}

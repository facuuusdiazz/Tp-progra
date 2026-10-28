package negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class GrafoTest {

    private Grafo grafo;

    @Before
    public void setUp() {
        grafo = new Grafo();
    }

    @Test
    public void grafoNuevoEstaVacio() {
        assertEquals(0, grafo.cantidadVertices());
        assertEquals(0, grafo.cantidadAristas());
    }

    @Test
    public void losVerticesSeNumeranDesdeUno() {
        assertEquals(1, grafo.agregarVertice());
        assertEquals(2, grafo.agregarVertice());
        assertEquals(3, grafo.agregarVertice());
        assertEquals(3, grafo.cantidadVertices());
        assertTrue(grafo.existeVertice(2));
        assertFalse(grafo.existeVertice(4));
    }

    @Test
    public void laAristaEsSimetrica() {
        grafo.agregarVertice();
        grafo.agregarVertice();
        grafo.agregarArista(1, 2);

        assertTrue(grafo.existeArista(1, 2));
        assertTrue(grafo.existeArista(2, 1));
        assertEquals(1, grafo.cantidadAristas());
    }

    @Test
    public void agregarDosVecesLaMismaAristaNoLaDuplica() {
        grafo.agregarVertice();
        grafo.agregarVertice();
        grafo.agregarArista(1, 2);
        grafo.agregarArista(2, 1);

        assertEquals(1, grafo.cantidadAristas());
        assertEquals(1, grafo.grado(1));
    }

    @Test
    public void vecinosYGrado() {
        for (int i = 0; i < 4; i++) {
            grafo.agregarVertice();
        }
        grafo.agregarArista(1, 2);
        grafo.agregarArista(1, 3);

        assertEquals(new HashSet<>(Arrays.asList(2, 3)), grafo.vecinos(1));
        assertEquals(2, grafo.grado(1));
        assertEquals(0, grafo.grado(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aristaConExtremoInexistenteLanzaExcepcion() {
        grafo.agregarVertice();
        grafo.agregarArista(1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aristaConPrimerExtremoInexistenteLanzaExcepcion() {
        grafo.agregarVertice();
        grafo.agregarArista(7, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aristaDeUnVerticeConsigoMismoLanzaExcepcion() {
        grafo.agregarVertice();
        grafo.agregarArista(1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void vecinosDeVerticeInexistenteLanzaExcepcion() {
        grafo.vecinos(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void existeAristaConVerticeInexistenteLanzaExcepcion() {
        grafo.agregarVertice();
        grafo.existeArista(1, 2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void losVecinosNoSePuedenModificarDesdeAfuera() {
        grafo.agregarVertice();
        grafo.agregarVertice();
        grafo.vecinos(1).add(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void losVerticesNoSePuedenModificarDesdeAfuera() {
        grafo.agregarVertice();
        grafo.vertices().add(99);
    }
}

package controlador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import negocio.Grafo;
import negocio.Solver;
import negocio.SolverGoloso;

public class ControladorGrafoTest {

    private ControladorGrafo controlador;

    @Before
    public void setUp() {
        controlador = new ControladorGrafo(new SolverGoloso());
    }

    @Test
    public void empiezaConUnGrafoVacio() {
        assertEquals(0, controlador.cantidadVertices());
        assertEquals(0, controlador.cantidadAristas());
        assertTrue(controlador.aristas().isEmpty());
    }

    @Test
    public void agregarVerticesYAristas() {
        controlador.agregarVertice();
        controlador.agregarVertice();
        controlador.agregarArista(1, 2);

        assertEquals(2, controlador.cantidadVertices());
        assertEquals(1, controlador.cantidadAristas());
        assertTrue(controlador.existeArista(2, 1));
    }

    @Test
    public void aristasDevuelveCadaAristaUnaSolaVez() {
        for (int i = 0; i < 3; i++) {
            controlador.agregarVertice();
        }
        controlador.agregarArista(2, 1);
        controlador.agregarArista(3, 2);

        List<int[]> aristas = controlador.aristas();

        assertEquals(2, aristas.size());
        assertTrue(Arrays.equals(new int[] { 1, 2 }, aristas.get(0)));
        assertTrue(Arrays.equals(new int[] { 2, 3 }, aristas.get(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aristaConVerticeInexistenteLanzaExcepcion() {
        controlador.agregarVertice();
        controlador.agregarArista(1, 2);
    }

    @Test
    public void calculaElConjuntoDominanteDeLaFigura1() {
        for (int i = 0; i < 6; i++) {
            controlador.agregarVertice();
        }
        int[][] aristas = { { 6, 4 }, { 4, 5 }, { 4, 3 }, { 5, 1 }, { 5, 2 }, { 1, 2 }, { 2, 3 } };
        for (int[] a : aristas) {
            controlador.agregarArista(a[0], a[1]);
        }

        assertEquals(new HashSet<>(Arrays.asList(2, 4)), controlador.calcularConjuntoDominante());
    }

    @Test
    public void nuevoGrafoDescartaTodo() {
        controlador.agregarVertice();
        controlador.agregarVertice();
        controlador.agregarArista(1, 2);

        controlador.nuevoGrafo();

        assertEquals(0, controlador.cantidadVertices());
        assertEquals(0, controlador.cantidadAristas());
        assertEquals(1, controlador.agregarVertice());
    }

    @Test
    public void usaElSolverQueRecibePorConstructor() {
        Solver solverFalso = new Solver() {
            @Override
            public Set<Integer> resolver(Grafo grafo) {
                return new HashSet<>(Arrays.asList(42));
            }
        };
        ControladorGrafo conSolverFalso = new ControladorGrafo(solverFalso);

        assertEquals(new HashSet<>(Arrays.asList(42)), conSolverFalso.calcularConjuntoDominante());
    }

    @Test(expected = IllegalArgumentException.class)
    public void solverNullLanzaExcepcion() {
        new ControladorGrafo(null);
    }
}

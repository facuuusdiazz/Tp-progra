package interfaz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

/**
 * Panel que dibuja un grafo con sus vértices distribuidos en círculo y resalta
 * el conjunto dominante. Es una vista pasiva: solo dibuja los datos que le
 * pasan con mostrar(...), no conoce al controlador ni al negocio.
 */
public class PanelGrafo extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int MARGEN = 40;
    private static final int RADIO_MAXIMO = 20;
    private static final int RADIO_MINIMO = 5;
    private static final int RADIO_MINIMO_CON_NUMERO = 9;

    private static final Color COLOR_ARISTA = new Color(160, 160, 160);
    private static final Color COLOR_ARISTA_DOMINANTE = new Color(230, 140, 20);
    private static final Color COLOR_VERTICE = new Color(205, 225, 250);
    private static final Color COLOR_VERTICE_DOMINANTE = new Color(250, 175, 40);
    private static final Color COLOR_BORDE = new Color(50, 50, 50);

    private List<Integer> vertices = new ArrayList<>();
    private List<int[]> aristas = new ArrayList<>();
    private Set<Integer> dominante = new HashSet<>();

    public PanelGrafo() {
        setBackground(Color.WHITE);
    }

    /**
     * @param vertices  vértices del grafo
     * @param aristas   aristas como pares {i, j}
     * @param dominante vértices a resaltar (vacío si no hay resultado)
     */
    public void mostrar(Set<Integer> vertices, List<int[]> aristas, Set<Integer> dominante) {
        // Se copian los datos para no depender de colecciones que cambian afuera.
        this.vertices = new ArrayList<>(vertices);
        this.aristas = new ArrayList<>(aristas);
        this.dominante = new HashSet<>(dominante);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (vertices.isEmpty()) {
            dibujarTextoCentrado(g2, "Agregue vértices para ver el grafo", getWidth() / 2, getHeight() / 2);
        } else {
            Map<Integer, Point> posiciones = calcularPosiciones();
            int radio = radioVertice();
            dibujarAristas(g2, posiciones);
            dibujarVertices(g2, posiciones, radio);
            if (!dominante.isEmpty()) {
                dibujarLeyenda(g2);
            }
        }
        g2.dispose();
    }

    // ---------- geometría ----------

    private int radioCirculo() {
        return Math.max(10, Math.min(getWidth(), getHeight()) / 2 - MARGEN);
    }

    private Map<Integer, Point> calcularPosiciones() {
        int n = vertices.size();
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        int radioCirculo = radioCirculo();

        Map<Integer, Point> posiciones = new HashMap<>();
        for (int k = 0; k < n; k++) {
            if (n == 1) {
                posiciones.put(vertices.get(k), new Point(centroX, centroY));
            } else {
                double angulo = -Math.PI / 2 + 2 * Math.PI * k / n;
                int x = centroX + (int) Math.round(radioCirculo * Math.cos(angulo));
                int y = centroY + (int) Math.round(radioCirculo * Math.sin(angulo));
                posiciones.put(vertices.get(k), new Point(x, y));
            }
        }
        return posiciones;
    }

    // El círculo del vértice se achica si hay muchos, para que no se superpongan.
    private int radioVertice() {
        int n = vertices.size();
        if (n == 1) {
            return RADIO_MAXIMO;
        }
        double distanciaEntreVecinos = 2 * radioCirculo() * Math.sin(Math.PI / n);
        int radio = (int) (distanciaEntreVecinos * 0.4);
        return Math.max(RADIO_MINIMO, Math.min(RADIO_MAXIMO, radio));
    }

    // ---------- dibujo ----------

    private void dibujarAristas(Graphics2D g2, Map<Integer, Point> posiciones) {
        for (int[] arista : aristas) {
            boolean tocaDominante = dominante.contains(arista[0]) || dominante.contains(arista[1]);
            g2.setColor(tocaDominante ? COLOR_ARISTA_DOMINANTE : COLOR_ARISTA);
            g2.setStroke(new BasicStroke(tocaDominante ? 2.2f : 1.2f));
            Point a = posiciones.get(arista[0]);
            Point b = posiciones.get(arista[1]);
            g2.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    private void dibujarVertices(Graphics2D g2, Map<Integer, Point> posiciones, int radio) {
        g2.setStroke(new BasicStroke(1.5f));
        for (int v : vertices) {
            Point p = posiciones.get(v);
            boolean esDominante = dominante.contains(v);

            g2.setColor(esDominante ? COLOR_VERTICE_DOMINANTE : COLOR_VERTICE);
            g2.fillOval(p.x - radio, p.y - radio, 2 * radio, 2 * radio);
            g2.setColor(COLOR_BORDE);
            g2.drawOval(p.x - radio, p.y - radio, 2 * radio, 2 * radio);

            if (radio >= RADIO_MINIMO_CON_NUMERO) {
                dibujarTextoCentrado(g2, String.valueOf(v), p.x, p.y);
            }
        }
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int x = 14;
        int y = getHeight() - 38;
        g2.setStroke(new BasicStroke(1.2f));

        g2.setColor(COLOR_VERTICE_DOMINANTE);
        g2.fillOval(x, y, 14, 14);
        g2.setColor(COLOR_BORDE);
        g2.drawOval(x, y, 14, 14);
        g2.drawString("En el conjunto dominante", x + 22, y + 12);

        g2.setColor(COLOR_VERTICE);
        g2.fillOval(x, y + 20, 14, 14);
        g2.setColor(COLOR_BORDE);
        g2.drawOval(x, y + 20, 14, 14);
        g2.drawString("Dominado por un vecino", x + 22, y + 32);
    }

    private void dibujarTextoCentrado(Graphics2D g2, String texto, int centroX, int centroY) {
        g2.setColor(COLOR_BORDE);
        FontMetrics fm = g2.getFontMetrics();
        int x = centroX - fm.stringWidth(texto) / 2;
        int y = centroY + (fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(texto, x, y);
    }
}

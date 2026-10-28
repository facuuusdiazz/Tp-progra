package interfaz;
 
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
 
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
 
import controlador.ControladorGrafo;
import persistencia.GrafoJSON;
import persistencia.SerializadorGrafo;
 
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * Ventana principal: carga manual del grafo, cálculo del conjunto dominante y
 * visualización. No contiene lógica de negocio; todo pasa por el
 * ControladorGrafo.
 */
public class VentanaPrincipal extends JFrame {
 
    private static final long serialVersionUID = 1L;
    private static final String TEXTO_INICIAL_RESULTADO =
            "Presione «Calcular conjunto dominante» para ver el resultado.";
 
    private final ControladorGrafo controlador;
    private PanelGrafo panelGrafo;
    private JTextArea areaGrafo;
    private JLabel lblResultado;
 
    public VentanaPrincipal(ControladorGrafo controlador) {
        this.controlador = controlador;
        configurarVentana();
        add(crearPanelBotones(), BorderLayout.WEST);
        add(crearPanelVisualizacion(), BorderLayout.CENTER);
        add(crearPanelLista(), BorderLayout.EAST);
        add(crearPanelResultado(), BorderLayout.SOUTH);
        actualizarVista(Collections.<Integer>emptySet());
    }
 
    // ---------- construcción de la interfaz ----------
 
    private void configurarVentana() {
        setTitle("Conjunto dominante mínimo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 650); // por debajo del máximo de 1366 x 768
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
    }
 
    private JPanel crearPanelBotones() {
        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 10));
        botones.add(crearBoton("Agregar vértice", this::agregarVertice));
        botones.add(crearBoton("Agregar arista", this::agregarArista));
        botones.add(crearBoton("Calcular conjunto dominante", this::calcular));
        botones.add(crearBoton("Nuevo grafo", this::nuevoGrafo));
        
        // -- BOTONES NUEVOS --
        botones.add(crearBoton("Guardar grafo (JSON)", this::guardarJSON));
        botones.add(crearBoton("Cargar grafo (JSON)", this::cargarJSON));
 
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(botones, BorderLayout.NORTH);
        return contenedor;
    } 
 
    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }
 
    private JPanel crearPanelVisualizacion() {
        panelGrafo = new PanelGrafo();
 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Visualización"));
        panel.add(panelGrafo, BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel crearPanelLista() {
        areaGrafo = new JTextArea();
        areaGrafo.setEditable(false);
        areaGrafo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        areaGrafo.setMargin(new Insets(8, 8, 8, 8));
 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Grafo cargado"));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.add(new JScrollPane(areaGrafo), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel crearPanelResultado() {
        lblResultado = new JLabel(TEXTO_INICIAL_RESULTADO);
        lblResultado.setFont(lblResultado.getFont().deriveFont(Font.BOLD, 14f));
 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultado"));
        panel.add(lblResultado, BorderLayout.CENTER);
        return panel;
    }
 
    // ---------- acciones ----------
 
    private void agregarVertice() {
        controlador.agregarVertice();
        grafoCambio();
    }
 
    private void agregarArista() {
        if (controlador.cantidadVertices() < 2) {
            mostrarAviso("Se necesitan al menos dos vértices para agregar una arista.");
            return;
        }
 
        Integer[] vertices = controlador.vertices().toArray(new Integer[0]);
        JComboBox<Integer> primerExtremo = new JComboBox<>(vertices);
        JComboBox<Integer> segundoExtremo = new JComboBox<>(vertices);
        segundoExtremo.setSelectedIndex(1);
 
        Object[] contenido = { "Primer extremo:", primerExtremo, "Segundo extremo:", segundoExtremo };
        int opcion = JOptionPane.showConfirmDialog(this, contenido, "Agregar arista",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
 
        int i = (Integer) primerExtremo.getSelectedItem();
        int j = (Integer) segundoExtremo.getSelectedItem();
        try {
            if (controlador.existeArista(i, j)) {
                mostrarAviso("La arista " + i + " - " + j + " ya existe.");
                return;
            }
            controlador.agregarArista(i, j);
            grafoCambio();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo agregar la arista",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void calcular() {
        if (controlador.cantidadVertices() == 0) {
            mostrarAviso("Agregue al menos un vértice antes de calcular.");
            return;
        }
        Set<Integer> conjunto = controlador.calcularConjuntoDominante();
        lblResultado.setText("Conjunto dominante (tamaño " + conjunto.size() + "): { "
                + unir(conjunto) + " }");
        actualizarVista(conjunto);
    }
 
    private void nuevoGrafo() {
        int opcion = JOptionPane.showConfirmDialog(this, "Se va a descartar el grafo actual. ¿Continuar?",
                "Nuevo grafo", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            controlador.nuevoGrafo();
            grafoCambio();
        }
        
    }
    
    private void guardarJSON() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".json")) {
                ruta += ".json"; // Asegurarnos de que tenga la extensión
            }
            try {
                controlador.guardarGrafo(ruta);
                mostrarAviso("Grafo guardado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarJSON() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.cargarGrafo(chooser.getSelectedFile().getAbsolutePath());
                grafoCambio(); // Refresca la vista y borra resultados anteriores
                mostrarAviso("Grafo cargado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    // ---------- vista ----------
 
    // Si el grafo cambió, el resultado anterior deja de ser válido.
    private void grafoCambio() {
        lblResultado.setText(TEXTO_INICIAL_RESULTADO);
        actualizarVista(Collections.<Integer>emptySet());
    }
 
    private void actualizarVista(Set<Integer> dominante) {
        panelGrafo.mostrar(controlador.vertices(), controlador.aristas(), dominante);
 
        StringBuilder texto = new StringBuilder();
        texto.append("Vértices (").append(controlador.cantidadVertices()).append("):\n")
                .append(unir(controlador.vertices())).append("\n\n");
        texto.append("Aristas (").append(controlador.cantidadAristas()).append("):\n");
        for (int[] arista : controlador.aristas()) {
            texto.append("  ").append(arista[0]).append(" - ").append(arista[1]).append("\n");
        }
        areaGrafo.setText(texto.toString());
        areaGrafo.setCaretPosition(0);
    }
 
    private String unir(Set<Integer> numeros) {
        return numeros.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }
 
    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
}

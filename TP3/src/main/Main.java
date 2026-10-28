package main;

import javax.swing.SwingUtilities;

import controlador.ControladorGrafo;
import interfaz.VentanaPrincipal;
import negocio.SolverGoloso;

/** Punto de entrada: arma las piezas y abre la ventana. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControladorGrafo controlador = new ControladorGrafo(new SolverGoloso());
            new VentanaPrincipal(controlador).setVisible(true);
        });
    }
}

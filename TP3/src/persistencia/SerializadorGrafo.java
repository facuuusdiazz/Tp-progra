package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Serializador propio basado en texto plano estructurado.
 * Evita cualquier conflicto de compatibilidad con versiones antiguas de librerías en Java 21.
 */
public class SerializadorGrafo {

    public static void guardar(GrafoJSON datos, String ruta) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            writer.write("CANTIDAD_VERTICES:" + datos.cantidadVertices);
            writer.newLine();
            writer.write("ARISTAS:");
            writer.newLine();
            if (datos.aristas != null) {
                for (int[] arista : datos.aristas) {
                    writer.write(arista[0] + "," + arista[1]);
                    writer.newLine();
                }
            }
        }
    }

    public static GrafoJSON cargar(String ruta) throws IOException {
        GrafoJSON datos = new GrafoJSON();
        datos.aristas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea = reader.readLine();
            if (linea != null && linea.startsWith("CANTIDAD_VERTICES:")) {
                datos.cantidadVertices = Integer.parseInt(linea.split(":")[1].trim());
            }

            // Ignoramos la línea "ARISTAS:"
            reader.readLine();

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(",");
                    int u = Integer.parseInt(partes[0].trim());
                    int v = Integer.parseInt(partes[1].trim());
                    datos.aristas.add(new int[] { u, v });
                }
            }
        }
        return datos;
    }
}
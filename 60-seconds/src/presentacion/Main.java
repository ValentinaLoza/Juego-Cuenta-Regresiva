package presentacion;

import javax.swing.SwingUtilities;
import logica.RepositorioEventos;
import logica.RepositorioPartidas;
import logica.ServicioHistorial;
import persistencia.RepositorioEventosEnMemoria;
import persistencia.RepositorioPartidasEnMemoria;

/**
 * Punto de entrada. Acá se "arman" las capas: se eligen las implementaciones
 * de persistencia (hoy en memoria) y se le pasan a la lógica y a la ventana.
 * Para guardar en archivos o en una base de datos, solo cambiarían estas líneas.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RepositorioEventos eventos = new RepositorioEventosEnMemoria();
            RepositorioPartidas partidas = new RepositorioPartidasEnMemoria();
            ServicioHistorial historial = new ServicioHistorial(partidas);

            FrmPrincipal ventana = new FrmPrincipal(eventos, historial);
            ventana.setVisible(true);
        });
    }
}

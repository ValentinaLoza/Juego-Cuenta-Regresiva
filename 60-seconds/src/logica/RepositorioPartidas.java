package logica;

import java.util.List;

/**
 * Dónde se guardan las partidas jugadas. La lógica solo conoce esta interfaz;
 * hoy se implementa con un ArrayList, mañana podría ser un archivo o una base
 * de datos sin cambiar ServicioHistorial ni la interfaz gráfica.
 */
public interface RepositorioPartidas {
    void guardar(RegistroPartida partida);

    List<RegistroPartida> listar();

    /** @return la partida, o null si no existe */
    RegistroPartida buscarPorId(int id);

    /** Persiste los cambios hechos a una partida ya guardada. */
    void actualizar(RegistroPartida partida);

    /** @return true si existía y se eliminó */
    boolean eliminar(int id);
}

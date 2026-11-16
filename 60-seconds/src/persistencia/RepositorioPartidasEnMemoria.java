package persistencia;

import java.util.ArrayList;
import java.util.List;
import logica.RegistroPartida;
import logica.RepositorioPartidas;

/**
 * Guarda las partidas jugadas en un ArrayList (duran mientras el programa
 * está abierto). Es la única clase que conoce cómo se almacenan.
 */
public class RepositorioPartidasEnMemoria implements RepositorioPartidas {
    private final ArrayList<RegistroPartida> partidas = new ArrayList<>();
    private int ultimoId = 0;

    @Override
    public void guardar(RegistroPartida partida) {
        if (partida == null) throw new IllegalArgumentException("No se puede guardar una partida vacía");
        partida.asignarId(++ultimoId);
        partidas.add(partida);
    }

    @Override
    public List<RegistroPartida> listar() {
        return new ArrayList<>(partidas); // copia: nadie de afuera modifica la colección
    }

    @Override
    public RegistroPartida buscarPorId(int id) {
        for (RegistroPartida p : partidas) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    @Override
    public void actualizar(RegistroPartida partida) {
        // En memoria el objeto ya está modificado; con un archivo o una base
        // de datos acá se escribirían los cambios.
        if (buscarPorId(partida.getId()) == null) {
            throw new IllegalArgumentException("La partida " + partida.getId() + " no está guardada");
        }
    }

    @Override
    public boolean eliminar(int id) {
        return partidas.removeIf(p -> p.getId() == id);
    }
}

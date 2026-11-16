package logica;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Operaciones sobre el historial de partidas: registrar, buscar, listar,
 * renombrar y eliminar. Contiene las reglas; el guardado lo delega en el
 * RepositorioPartidas que recibe (Inversión de Dependencias).
 */
public class ServicioHistorial {
    private final RepositorioPartidas repositorio;

    public ServicioHistorial(RepositorioPartidas repositorio) {
        if (repositorio == null) throw new IllegalArgumentException("Falta el repositorio");
        this.repositorio = repositorio;
    }

    /** Registra el resultado de una partida terminada a nombre de un jugador. */
    public RegistroPartida registrar(String jugador, GestorPartida partida) {
        if (partida.getResultado() == null) {
            throw new IllegalArgumentException("La partida todavía no terminó.");
        }
        int sobrevivientes = partida.getResultado() == ResultadoPartida.NO_LLEGO
                ? 0 : partida.getRefugio().getVivos().size();
        RegistroPartida registro = new RegistroPartida(jugador, partida.getResultado(),
                partida.getRefugio().getDiasSupervividos(), sobrevivientes);
        repositorio.guardar(registro);
        return registro;
    }

    /** Todas las partidas, de mayor a menor puntaje. */
    public List<RegistroPartida> listarPorPuntaje() {
        List<RegistroPartida> lista = new ArrayList<>(repositorio.listar());
        lista.sort(Comparator.comparingInt(RegistroPartida::getPuntaje).reversed());
        return lista;
    }

    /** Partidas cuyo jugador contiene el texto buscado (sin importar mayúsculas). */
    public List<RegistroPartida> buscarPorJugador(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listarPorPuntaje();
        String buscado = texto.trim().toLowerCase();
        List<RegistroPartida> encontradas = new ArrayList<>();
        for (RegistroPartida p : listarPorPuntaje()) {
            if (p.getJugador().toLowerCase().contains(buscado)) encontradas.add(p);
        }
        return encontradas;
    }

    public void renombrar(int id, String nuevoNombre) {
        RegistroPartida p = buscarExistente(id);
        p.setJugador(nuevoNombre); // valida el nombre
        repositorio.actualizar(p);
    }

    public void eliminar(int id) {
        buscarExistente(id);
        repositorio.eliminar(id);
    }

    /** La partida con más puntaje, o null si no hay ninguna. */
    public RegistroPartida mejorPartida() {
        List<RegistroPartida> lista = listarPorPuntaje();
        return lista.isEmpty() ? null : lista.get(0);
    }

    public int cantidadPartidas() {
        return repositorio.listar().size();
    }

    private RegistroPartida buscarExistente(int id) {
        RegistroPartida p = repositorio.buscarPorId(id);
        if (p == null) throw new IllegalArgumentException("No existe la partida número " + id + ".");
        return p;
    }
}

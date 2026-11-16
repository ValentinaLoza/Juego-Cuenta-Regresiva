package logica;

import java.util.List;

/**
 * De dónde salen los eventos del refugio. La lógica depende de esta
 * abstracción y no de una implementación concreta (Inversión de Dependencias).
 */
public interface RepositorioEventos {
    List<Evento> obtenerTodos();
}

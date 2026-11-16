package logica;

import java.util.Random;

/** Se pierde un objeto de las provisiones (si lo hay). */
public class EfectoPerdida extends Efecto {
    private final TipoItem tipo;

    public EfectoPerdida(TipoItem tipo) {
        this.tipo = tipo;
    }

    @Override
    public String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar) {
        return inventario.consumirRecurso(tipo) ? "- 1 " + tipo.getNombre() : null;
    }
}

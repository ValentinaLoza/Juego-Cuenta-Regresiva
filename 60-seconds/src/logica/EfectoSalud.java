package logica;

import java.util.Random;

/** Suma o resta salud al familiar del evento, o a toda la familia. */
public class EfectoSalud extends Efecto {
    private final int cantidad;
    private final boolean aTodos;

    public EfectoSalud(int cantidad, boolean aTodos) {
        this.cantidad = cantidad;
        this.aTodos = aTodos;
    }

    @Override
    public String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar) {
        if (cantidad == 0) return null;
        String signo = cantidad > 0 ? "+" : "";
        if (aTodos) {
            for (Persona p : refugio.getVivos()) p.modificarSalud(cantidad);
            return signo + cantidad + " salud a toda la familia";
        }
        objetivo.modificarSalud(cantidad);
        return signo + cantidad + " salud (" + objetivo.getNombre() + ")";
    }
}

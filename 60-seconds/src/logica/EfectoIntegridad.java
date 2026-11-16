package logica;

import java.util.Random;

/** Repara o daña el refugio. */
public class EfectoIntegridad extends Efecto {
    private final int cantidad;

    public EfectoIntegridad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar) {
        if (cantidad == 0) return null;
        refugio.modificarIntegridad(cantidad);
        return (cantidad > 0 ? "+" : "") + cantidad + " refugio";
    }
}

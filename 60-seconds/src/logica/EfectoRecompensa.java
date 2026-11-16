package logica;

import java.util.Random;

/** Agrega objetos a las provisiones. */
public class EfectoRecompensa extends Efecto {
    private final TipoItem tipo;
    private final int cantidad;

    public EfectoRecompensa(TipoItem tipo, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La recompensa debe ser de al menos 1 objeto");
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    @Override
    public String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar) {
        for (int i = 0; i < cantidad; i++) {
            inventario.agregarItem(new Item("R" + azar.nextInt(100000), tipo));
        }
        return "+ " + cantidad + " " + tipo.getNombre();
    }
}

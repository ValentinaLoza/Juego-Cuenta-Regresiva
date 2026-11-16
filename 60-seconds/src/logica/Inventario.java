package logica;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * Las provisiones guardadas en el refugio.
 *
 * El agua y la comida se pueden repartir de a mitades: cuando se usa media
 * botella (o media lata), la otra mitad queda "abierta" para otro.
 * Por eso esas cantidades se cuentan en MITADES (1 botella = 2 mitades).
 */
public class Inventario {
    private ArrayList<Item> itemsObtenidos = new ArrayList<>();
    private Map<TipoItem, Integer> mitadesAbiertas = new EnumMap<>(TipoItem.class);

    public void agregarItem(Item item) {
        itemsObtenidos.add(item);
    }

    /** Cuántos ítems enteros de un tipo hay. */
    public int contar(TipoItem tipo) {
        int cantidad = 0;
        for (Item item : itemsObtenidos) {
            if (item.getTipo() == tipo) cantidad++;
        }
        return cantidad;
    }

    /** Cuántas mitades hay (enteros x 2 + la mitad abierta, si hay). */
    public int getMitades(TipoItem tipo) {
        return contar(tipo) * 2 + mitadesAbiertas.getOrDefault(tipo, 0);
    }

    /** Texto con la cantidad, ej: "2½". */
    public String cantidadTexto(TipoItem tipo) {
        return textoMitades(getMitades(tipo));
    }

    public static String textoMitades(int mitades) {
        int enteros = mitades / 2;
        boolean media = mitades % 2 == 1;
        if (enteros == 0 && media) return "½";
        return enteros + (media ? "½" : "");
    }

    public boolean tieneRecurso(TipoItem tipo) {
        return tipo.esRacion() ? getMitades(tipo) >= 2 : contar(tipo) > 0;
    }

    /** Gasta una unidad entera (para agua/comida: 2 mitades). */
    public boolean consumirRecurso(TipoItem tipo) {
        if (tipo.esRacion()) return consumirMitades(tipo, 2);
        return quitarUno(tipo);
    }

    /** Gasta mitades de agua o comida, abriendo botellas/latas si hace falta. */
    public boolean consumirMitades(TipoItem tipo, int mitades) {
        if (mitades <= 0) return true;
        if (getMitades(tipo) < mitades) return false;
        int abiertas = mitadesAbiertas.getOrDefault(tipo, 0);
        while (mitades > 0) {
            if (abiertas == 0) {   // abrir una nueva
                quitarUno(tipo);
                abiertas = 2;
            }
            abiertas--;
            mitades--;
        }
        mitadesAbiertas.put(tipo, abiertas);
        return true;
    }

    private boolean quitarUno(TipoItem tipo) {
        for (int i = 0; i < itemsObtenidos.size(); i++) {
            if (itemsObtenidos.get(i).getTipo() == tipo) {
                itemsObtenidos.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getCantidadTotal() {
        return itemsObtenidos.size();
    }

    public ArrayList<Item> getItemsObtenidos() {
        return itemsObtenidos;
    }
}

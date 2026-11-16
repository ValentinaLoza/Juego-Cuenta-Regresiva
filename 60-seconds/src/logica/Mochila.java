package logica;

import java.util.ArrayList;
import java.util.List;

/** La mochila del protagonista: tiene una cantidad fija de espacios. */
public class Mochila {
    public static final int CAPACIDAD = 8;

    private ArrayList<Cargable> contenido = new ArrayList<>();

    public int getEspacioUsado() {
        int usado = 0;
        for (Cargable c : contenido) usado += c.getTamano();
        return usado;
    }

    public int getEspacioLibre() {
        return CAPACIDAD - getEspacioUsado();
    }

    public boolean puedeCargar(Cargable c) {
        return c.getTamano() <= getEspacioLibre();
    }

    public boolean cargar(Cargable c) {
        if (!puedeCargar(c)) return false;
        contenido.add(c);
        return true;
    }

    public boolean quitar(Cargable c) {
        return contenido.remove(c);
    }

    public boolean estaVacia() {
        return contenido.isEmpty();
    }

    /** Saca todo de la mochila y lo devuelve. */
    public List<Cargable> vaciar() {
        List<Cargable> todo = new ArrayList<>(contenido);
        contenido.clear();
        return todo;
    }

    public List<Cargable> getContenido() {
        return contenido;
    }
}

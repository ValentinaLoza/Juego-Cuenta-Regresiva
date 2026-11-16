package logica;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Habitacion {
    private String codigo;
    private String nombre;
    private boolean esRefugio;
    private ArrayList<Item> itemsDisponibles = new ArrayList<>();
    private ArrayList<Persona> personas = new ArrayList<>();
    private ArrayList<Salida> salidas = new ArrayList<>();

    // Lugares donde puede aparecer algo: {x, y}. y >= 0.8 es el piso (ahí pueden pararse personas)
    private ArrayList<double[]> puntos = new ArrayList<>();

    // Ubicación en el plano de la casa (en celdas) para dibujar el minimapa
    private int planoX, planoY, planoAncho;

    public Habitacion(String codigo, String nombre, int planoX, int planoY, int planoAncho) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.planoX = planoX;
        this.planoY = planoY;
        this.planoAncho = planoAncho;
    }

    public void marcarComoRefugio() { this.esRefugio = true; }

    public void agregarSalida(Habitacion destino, TipoSalida tipo, double posX) {
        salidas.add(new Salida(destino, tipo, posX));
    }

    public void agregarPunto(double x, double y) {
        puntos.add(new double[]{x, y});
    }

    /** Devuelve un punto que nadie esté usando, o null si no hay. */
    public double[] puntoLibre(Random azar, boolean soloPiso) {
        List<double[]> libres = new ArrayList<>();
        for (double[] p : puntos) {
            if (soloPiso && p[1] < 0.8) continue;
            if (!estaOcupado(p)) libres.add(p);
        }
        return libres.isEmpty() ? null : libres.get(azar.nextInt(libres.size()));
    }

    private boolean estaOcupado(double[] p) {
        for (Item it : itemsDisponibles) {
            if (Math.abs(it.getPosX() - p[0]) < 0.08 && Math.abs(it.getPosY() - p[1]) < 0.08) return true;
        }
        for (Persona per : personas) {
            if (Math.abs(per.getPosX() - p[0]) < 0.12 && per.getPosY() >= 0.8 && p[1] >= 0.8) return true;
        }
        return false;
    }

    public int cantidadPuntosLibres(boolean soloPiso) {
        int n = 0;
        for (double[] p : puntos) if ((!soloPiso || p[1] >= 0.8) && !estaOcupado(p)) n++;
        return n;
    }

    /** Deja un ítem en un lugar libre de la habitación (o en el piso si está todo ocupado). */
    public void dejarItem(Item item, Random azar) {
        double[] p = puntoLibre(azar, false);
        if (p == null) p = new double[]{0.2 + azar.nextDouble() * 0.6, 0.88 + azar.nextDouble() * 0.08};
        item.setPosicion(p[0], p[1]);
        itemsDisponibles.add(item);
    }

    public void dejarPersona(Persona persona, Random azar) {
        double[] p = puntoLibre(azar, true);
        if (p == null) p = new double[]{0.3 + azar.nextDouble() * 0.4, 0.95};
        persona.setPosicion(p[0], p[1]);
        personas.add(persona);
    }

    public boolean quitar(Cargable c) {
        return itemsDisponibles.remove(c) || personas.remove(c);
    }

    public boolean contiene(Cargable c) {
        return itemsDisponibles.contains(c) || personas.contains(c);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public boolean esRefugio() { return esRefugio; }
    public ArrayList<Item> getItemsDisponibles() { return itemsDisponibles; }
    public ArrayList<Persona> getPersonas() { return personas; }
    public ArrayList<Salida> getSalidas() { return salidas; }
    public int getPlanoX() { return planoX; }
    public int getPlanoY() { return planoY; }
    public int getPlanoAncho() { return planoAncho; }
}

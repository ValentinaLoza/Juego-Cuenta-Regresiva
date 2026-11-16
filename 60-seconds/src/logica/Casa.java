package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * La casa: sus habitaciones, cómo se conectan y dónde aparecen las cosas.
 *
 * Se separó de GestorPartida para que cada clase tenga una sola
 * responsabilidad: la Casa sabe de habitaciones y caminos; el gestor
 * coordina la partida.
 */
public class Casa {
    private final ArrayList<Habitacion> habitaciones = new ArrayList<>();
    private Habitacion recibidor;
    private Habitacion refugio;

    public Casa() {
        // Plano (columna, fila, ancho):
        //   [Dormitorio][Cuarto chicos][Baño]
        //   [            Pasillo            ]
        //   [  Living  ][  Recibidor  ][Cocina][Garage]
        //               [   Refugio   ]
        Habitacion dormitorio = new Habitacion("DOR", "Dormitorio", 0, 0, 1);
        Habitacion chicos = new Habitacion("CHI", "Cuarto de los chicos", 1, 0, 1);
        Habitacion bano = new Habitacion("BAN", "Baño", 2, 0, 1);
        Habitacion pasillo = new Habitacion("PAS", "Pasillo", 0, 1, 3);
        Habitacion living = new Habitacion("LIV", "Living", 0, 2, 1);
        recibidor = new Habitacion("REC", "Recibidor", 1, 2, 1);
        Habitacion cocina = new Habitacion("COC", "Cocina", 2, 2, 1);
        Habitacion garage = new Habitacion("GAR", "Garage", 3, 2, 1);
        refugio = new Habitacion("REF", "Refugio", 1, 3, 1);
        refugio.marcarComoRefugio();

        // Salidas (destino, tipo, posición en la pared)
        recibidor.agregarSalida(living, TipoSalida.PUERTA, 0.12);
        recibidor.agregarSalida(pasillo, TipoSalida.PUERTA, 0.50);
        recibidor.agregarSalida(cocina, TipoSalida.ARCADA, 0.88);
        recibidor.agregarSalida(refugio, TipoSalida.ESCOTILLA, 0.50);
        living.agregarSalida(recibidor, TipoSalida.PUERTA, 0.88);
        cocina.agregarSalida(recibidor, TipoSalida.ARCADA, 0.10);
        cocina.agregarSalida(garage, TipoSalida.PUERTA, 0.90);
        garage.agregarSalida(cocina, TipoSalida.PUERTA, 0.10);
        pasillo.agregarSalida(dormitorio, TipoSalida.PUERTA, 0.14);
        pasillo.agregarSalida(chicos, TipoSalida.PUERTA, 0.40);
        pasillo.agregarSalida(bano, TipoSalida.PUERTA, 0.66);
        pasillo.agregarSalida(recibidor, TipoSalida.ARCADA, 0.90);
        dormitorio.agregarSalida(pasillo, TipoSalida.PUERTA, 0.88);
        chicos.agregarSalida(pasillo, TipoSalida.PUERTA, 0.12);
        bano.agregarSalida(pasillo, TipoSalida.PUERTA, 0.88);
        refugio.agregarSalida(recibidor, TipoSalida.ESCALERA, 0.50);

        // Lugares donde pueden aparecer cosas (x, y). y >= 0.8 es el piso.
        puntos(recibidor, 0.71, 0.50, 0.20, 0.95, 0.66, 0.92, 0.86, 0.96);
        puntos(living, 0.20, 0.50, 0.40, 0.50, 0.73, 0.45, 0.32, 0.92, 0.55, 0.95, 0.74, 0.93);
        puntos(cocina, 0.28, 0.42, 0.43, 0.42, 0.25, 0.93, 0.45, 0.96, 0.84, 0.93);
        puntos(garage, 0.28, 0.47, 0.48, 0.47, 0.82, 0.40, 0.72, 0.60, 0.35, 0.93, 0.60, 0.96);
        puntos(pasillo, 0.53, 0.95, 0.78, 0.94);
        puntos(dormitorio, 0.30, 0.45, 0.45, 0.45, 0.63, 0.53, 0.25, 0.94, 0.50, 0.96);
        puntos(chicos, 0.42, 0.50, 0.80, 0.31, 0.80, 0.57, 0.58, 0.70, 0.40, 0.95, 0.66, 0.95);
        puntos(bano, 0.20, 0.51, 0.57, 0.43, 0.30, 0.94, 0.55, 0.96);
        puntos(refugio, 0.15, 0.93, 0.27, 0.96, 0.73, 0.96, 0.85, 0.93);

        Collections.addAll(habitaciones, recibidor, living, cocina, garage, pasillo,
                dormitorio, chicos, bano, refugio);
    }

    private void puntos(Habitacion h, double... xy) {
        for (int i = 0; i < xy.length; i += 2) h.agregarPunto(xy[i], xy[i + 1]);
    }

    /** Ubica a la familia y a los objetos en lugares al azar (distintos en cada partida). */
    public void repartir(List<Persona> familia, TipoItem[] objetos, Random azar) {
        List<Habitacion> lugares = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (h != recibidor && !h.esRefugio()) lugares.add(h);
        }

        // Cada familiar en una habitación distinta
        List<Habitacion> paraFamilia = new ArrayList<>(lugares);
        Collections.shuffle(paraFamilia, azar);
        for (int i = 0; i < familia.size(); i++) {
            paraFamilia.get(i).dejarPersona(familia.get(i), azar);
        }

        // Los objetos: mezclados y cada uno a una habitación con lugar libre
        List<TipoItem> mezclados = new ArrayList<>();
        Collections.addAll(mezclados, objetos);
        Collections.shuffle(mezclados, azar);
        lugares.add(recibidor);
        int numero = 1;
        for (TipoItem tipo : mezclados) {
            List<Habitacion> conLugar = new ArrayList<>();
            for (Habitacion h : lugares) if (h.cantidadPuntosLibres(false) > 0) conLugar.add(h);
            if (conLugar.isEmpty()) break;
            Habitacion destino = conLugar.get(azar.nextInt(conLugar.size()));
            destino.dejarItem(new Item("I" + (numero++), tipo), azar);
        }
    }

    /** Cuántas puertas hay que cruzar desde "desde" hasta el refugio (búsqueda en anchura). */
    public int pasosHastaRefugio(Habitacion desde) {
        List<Habitacion> visitadas = new ArrayList<>();
        List<Habitacion> nivel = new ArrayList<>();
        nivel.add(desde);
        visitadas.add(desde);
        int pasos = 0;
        while (!nivel.isEmpty()) {
            for (Habitacion h : nivel) if (h.esRefugio()) return pasos;
            List<Habitacion> siguiente = new ArrayList<>();
            for (Habitacion h : nivel) {
                for (Salida s : h.getSalidas()) {
                    if (!visitadas.contains(s.getDestino())) {
                        visitadas.add(s.getDestino());
                        siguiente.add(s.getDestino());
                    }
                }
            }
            nivel = siguiente;
            pasos++;
        }
        return Integer.MAX_VALUE;
    }

    /** Busca una habitación por su código (ej: "COC"). */
    public Habitacion buscar(String codigo) {
        for (Habitacion h : habitaciones) {
            if (h.getCodigo().equalsIgnoreCase(codigo)) return h;
        }
        throw new IllegalArgumentException("No existe la habitación " + codigo);
    }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public Habitacion getRecibidor() { return recibidor; }
    public Habitacion getRefugio() { return refugio; }
}

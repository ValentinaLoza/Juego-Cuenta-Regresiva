package logica;

import java.util.ArrayList;

/**
 * Lo que pasa en un día del refugio. El texto puede usar {nombre}: el juego lo
 * reemplaza por el familiar al que le toca el evento.
 */
public class Evento {
    private String historia;
    private ArrayList<Opcion> opciones = new ArrayList<>();
    private boolean requiereNinos;     // Solo puede salir si hay un chico vivo en el refugio
    private boolean protagonizaUnNino; // El evento le pasa a uno de los chicos

    public Evento(String historia) {
        this.historia = historia;
    }

    public Evento soloConNinos() {
        this.requiereNinos = true;
        this.protagonizaUnNino = true;
        return this;
    }

    public void agregarOpcion(Opcion opcion) {
        opciones.add(opcion);
    }

    public String getHistoria(Persona objetivo) {
        return historia.replace("{nombre}", objetivo.getNombre());
    }

    public boolean isRequiereNinos() { return requiereNinos; }
    public boolean isProtagonizaUnNino() { return protagonizaUnNino; }
    public ArrayList<Opcion> getOpciones() { return opciones; }
}

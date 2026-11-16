package logica;

import java.util.ArrayList;
import java.util.List;

public class Refugio {
    public static final int DIAS_PARA_RESCATE = 15;    // Sobrevivir hasta este día = victoria
    public static final int CONTACTOS_PARA_RESCATE = 3; // O contactar por radio esta cantidad de veces

    private int integridad = 100;
    private int diasSupervividos = 1;
    private int contactosRadio;
    private ArrayList<Persona> personas = new ArrayList<>();

    public void agregarPersona(Persona p) {
        if (!personas.contains(p)) personas.add(p);
    }

    public void modificarIntegridad(int cantidad) {
        integridad = Math.max(0, Math.min(100, integridad + cantidad));
    }

    public void avanzarDia() { diasSupervividos++; }

    public void registrarContactoRadio(int cantidad) { contactosRadio += cantidad; }

    public List<Persona> getVivos() {
        List<Persona> vivos = new ArrayList<>();
        for (Persona p : personas) if (p.estaViva()) vivos.add(p);
        return vivos;
    }

    public boolean hayNinosVivos() {
        for (Persona p : getVivos()) if (p.getRol().esNino()) return true;
        return false;
    }

    public boolean esDerrota() {
        return integridad <= 0 || getVivos().isEmpty();
    }

    public boolean esVictoria() {
        return !esDerrota()
                && (diasSupervividos >= DIAS_PARA_RESCATE || contactosRadio >= CONTACTOS_PARA_RESCATE);
    }

    public int getIntegridad() { return integridad; }
    public int getDiasSupervividos() { return diasSupervividos; }
    public int getContactosRadio() { return contactosRadio; }
    public List<Persona> getPersonas() { return personas; }
}

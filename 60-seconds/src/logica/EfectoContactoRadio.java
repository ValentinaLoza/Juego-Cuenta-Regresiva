package logica;

import java.util.Random;

/** Suma un contacto con el rescate por radio. */
public class EfectoContactoRadio extends Efecto {

    @Override
    public String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar) {
        refugio.registrarContactoRadio(1);
        return "Contactos con el rescate: " + refugio.getContactosRadio() + "/" + Refugio.CONTACTOS_PARA_RESCATE;
    }
}

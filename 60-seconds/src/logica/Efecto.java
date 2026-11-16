package logica;

import java.util.Random;

/**
 * Una consecuencia de una decisión (perder salud, ganar agua, dañar el refugio...).
 *
 * Es abstracta porque "aplicar un efecto" siempre existe, pero cada tipo de
 * efecto lo hace a su manera. Opcion trabaja con una lista de Efecto sin saber
 * de qué clase es cada uno (polimorfismo): para agregar un efecto nuevo alcanza
 * con crear otra subclase, sin modificar Opcion (principio Abierto/Cerrado).
 */
public abstract class Efecto {

    /**
     * Aplica el efecto.
     *
     * @param refugio   el refugio (integridad, familia, radio)
     * @param inventario las provisiones guardadas
     * @param objetivo  el familiar al que le pasa el evento
     * @param azar      generador de números al azar
     * @return una línea que describe lo que pasó, o null si no hay nada que contar
     */
    public abstract String aplicar(Refugio refugio, Inventario inventario, Persona objetivo, Random azar);
}

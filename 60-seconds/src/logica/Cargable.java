package logica;

/** Algo que se puede llevar en la mochila: un objeto o una persona. */
public interface Cargable {
    String getNombre();

    /** Cuántos espacios de la mochila ocupa. */
    int getTamano();
}

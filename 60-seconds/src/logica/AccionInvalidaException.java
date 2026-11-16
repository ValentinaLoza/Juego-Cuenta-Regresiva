package logica;

/**
 * Se lanza cuando el jugador intenta algo que las reglas del juego no
 * permiten (agarrar algo que no entra en la mochila, curar sin botiquín...).
 * El mensaje está pensado para mostrarse tal cual en la pantalla.
 */
public class AccionInvalidaException extends Exception {
    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}

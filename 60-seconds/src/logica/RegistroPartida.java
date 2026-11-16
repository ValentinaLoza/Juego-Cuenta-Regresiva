package logica;

import java.time.LocalDateTime;

/** Una partida terminada, tal como se guarda en el historial. */
public class RegistroPartida {
    public static final int LARGO_MINIMO_NOMBRE = 2;
    public static final int LARGO_MAXIMO_NOMBRE = 20;

    private int id; // lo asigna el repositorio al guardarla
    private String jugador;
    private final LocalDateTime fecha;
    private final ResultadoPartida resultado;
    private final int diasSobrevividos;
    private final int sobrevivientes;

    public RegistroPartida(String jugador, ResultadoPartida resultado, int diasSobrevividos, int sobrevivientes) {
        if (resultado == null) throw new IllegalArgumentException("Falta el resultado de la partida");
        if (diasSobrevividos < 0) throw new IllegalArgumentException("Los días no pueden ser negativos");
        if (sobrevivientes < 0 || sobrevivientes > 4) {
            throw new IllegalArgumentException("Los sobrevivientes deben estar entre 0 y 4");
        }
        setJugador(jugador);
        this.resultado = resultado;
        this.diasSobrevividos = diasSobrevividos;
        this.sobrevivientes = sobrevivientes;
        this.fecha = LocalDateTime.now();
    }

    /**
     * Valida y normaliza el nombre de un jugador.
     * @throws IllegalArgumentException con un mensaje para mostrar si no es válido
     */
    public static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Escribí tu nombre.");
        }
        String limpio = nombre.trim().replaceAll("\\s+", " ");
        if (limpio.length() < LARGO_MINIMO_NOMBRE || limpio.length() > LARGO_MAXIMO_NOMBRE) {
            throw new IllegalArgumentException("El nombre debe tener entre " + LARGO_MINIMO_NOMBRE
                    + " y " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
        if (!limpio.matches("[\\p{L}0-9 ]+")) {
            throw new IllegalArgumentException("El nombre solo puede tener letras, números y espacios.");
        }
        return limpio;
    }

    /**
     * Puntaje de la partida (regla del juego): 10 por día sobrevivido,
     * 50 por cada sobreviviente y un extra según cómo terminó.
     */
    public int getPuntaje() {
        return diasSobrevividos * 10 + sobrevivientes * 50 + resultado.getBonificacion();
    }

    public void setJugador(String jugador) {
        this.jugador = validarNombre(jugador);
    }

    /** Solo el repositorio asigna el id, una única vez. */
    public void asignarId(int id) {
        if (this.id != 0) throw new IllegalStateException("La partida ya tiene id");
        if (id <= 0) throw new IllegalArgumentException("El id debe ser positivo");
        this.id = id;
    }

    public int getId() { return id; }
    public String getJugador() { return jugador; }
    public LocalDateTime getFecha() { return fecha; }
    public ResultadoPartida getResultado() { return resultado; }
    public int getDiasSobrevividos() { return diasSobrevividos; }
    public int getSobrevivientes() { return sobrevivientes; }
}

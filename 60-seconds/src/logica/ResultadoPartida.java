package logica;

/** Cómo terminó una partida. */
public enum ResultadoPartida {
    RESCATADOS("Rescatados por radio", 150),
    SOBREVIVIERON("Sobrevivieron", 100),
    MURIERON("Murieron en el refugio", 0),
    NO_LLEGO("No llegó al refugio", 0);

    private final String descripcion;
    private final int bonificacion; // puntos extra que da este final

    ResultadoPartida(String descripcion, int bonificacion) {
        this.descripcion = descripcion;
        this.bonificacion = bonificacion;
    }

    public String getDescripcion() { return descripcion; }
    public int getBonificacion() { return bonificacion; }

    @Override
    public String toString() { return descripcion; }
}

package logica;

/** Conexión de una habitación con otra (puerta, arcada, escotilla...). */
public class Salida {
    private Habitacion destino;
    private TipoSalida tipo;
    private double posX; // Posición horizontal en la pared (0.0 a 1.0)

    public Salida(Habitacion destino, TipoSalida tipo, double posX) {
        this.destino = destino;
        this.tipo = tipo;
        this.posX = posX;
    }

    public Habitacion getDestino() { return destino; }
    public TipoSalida getTipo() { return tipo; }
    public double getPosX() { return posX; }
}

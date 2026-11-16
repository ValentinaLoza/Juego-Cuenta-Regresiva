package logica;

/**
 * Un integrante de la familia. Tiene su propia salud, hidratación y
 * alimentación (de 0 a 100). Cuando una de esas barras llega a 0, empieza
 * a perder salud cada noche.
 */
public class Persona implements Cargable {
    private String nombre;
    private Rol rol;
    private int salud = 100;
    private int hidratacion = 100;
    private int alimentacion = 100;
    private int nochesSinAgua;   // noches seguidas con la hidratación en 0
    private int nochesSinComida; // noches seguidas con la alimentación en 0
    private double posX; // Dónde está parado en la habitación (0.0 a 1.0)
    private double posY;

    // Ración elegida para esta noche, en mitades: 0 = nada, 1 = media, 2 = entera
    private int racionAgua;
    private int racionComida;

    public Persona(String nombre, Rol rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    /** Una persona ocupa la mochila entera: hay que llevarla sola. */
    @Override public int getTamano() { return Mochila.CAPACIDAD; }
    @Override public String getNombre() { return nombre; }

    public Rol getRol() { return rol; }
    public boolean estaViva() { return salud > 0; }
    public int getSalud() { return salud; }
    public int getHidratacion() { return hidratacion; }
    public int getAlimentacion() { return alimentacion; }
    public int getNochesSinAgua() { return nochesSinAgua; }
    public int getNochesSinComida() { return nochesSinComida; }

    public void modificarSalud(int cantidad) {
        if (!estaViva()) return;
        salud = Math.max(0, Math.min(100, salud + cantidad));
    }

    /** Suma hidratación (por ejemplo +50 con media ración). */
    public void beber(int cantidad) {
        hidratacion = Math.min(100, hidratacion + cantidad);
    }

    public void comer(int cantidad) {
        alimentacion = Math.min(100, alimentacion + cantidad);
    }

    /**
     * Pasa una noche: bajan la hidratación y la alimentación.
     * @return la salud que perdió por sed y hambre
     */
    public int pasarNoche(int sed, int hambre) {
        hidratacion = Math.max(0, hidratacion - sed);
        alimentacion = Math.max(0, alimentacion - hambre);
        int dano = 0;
        if (hidratacion == 0) {
            nochesSinAgua++;
            dano += 10 * nochesSinAgua;
        } else {
            nochesSinAgua = 0;
        }
        if (alimentacion == 0) {
            nochesSinComida++;
            dano += 8 * nochesSinComida;
        } else {
            nochesSinComida = 0;
        }
        modificarSalud(-dano);
        return dano;
    }

    /** Texto corto del estado para mostrar en pantalla. */
    public String getEstado() {
        if (!estaViva()) return "Falleció";
        if (hidratacion == 0) return "Deshidratado";
        if (alimentacion == 0) return "Desnutrido";
        if (salud <= 25) return "Muy débil";
        if (hidratacion < 35) return "Con sed";
        if (alimentacion < 35) return "Con hambre";
        return "Bien";
    }

    public int getRacionAgua() { return racionAgua; }
    public void setRacionAgua(int mitades) { this.racionAgua = mitades; }
    public int getRacionComida() { return racionComida; }
    public void setRacionComida(int mitades) { this.racionComida = mitades; }

    public double getPosX() { return posX; }
    public double getPosY() { return posY; }

    public void setPosicion(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
}

package logica;

public class Item implements Cargable {
    private String codigo;
    private TipoItem tipo;
    private double posX; // Dónde está apoyado dentro de la habitación (0.0 a 1.0)
    private double posY;

    public Item(String codigo, TipoItem tipo) {
        this.codigo = codigo;
        this.tipo = tipo;
    }

    @Override public String getNombre() { return tipo.getNombre(); }
    @Override public int getTamano() { return tipo.getTamano(); }

    public String getCodigo() { return codigo; }
    public TipoItem getTipo() { return tipo; }
    public double getPosX() { return posX; }
    public double getPosY() { return posY; }

    public void setPosicion(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
}

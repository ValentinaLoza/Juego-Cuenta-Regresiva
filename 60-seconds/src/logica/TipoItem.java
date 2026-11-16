package logica;

/**
 * Categorías de objetos. Cada tipo tiene un nombre legible y cuántos
 * espacios ocupa en la mochila.
 */
public enum TipoItem {
    AGUA("Agua", 1),
    COMIDA("Lata de comida", 1),
    MEDICAMENTO("Botiquín", 2),
    HERRAMIENTA("Herramientas", 2),
    RADIO("Radio", 3),
    ARMA("Hacha", 2),
    MAPA("Mapa", 1),
    VARIOS("Mazo de cartas", 1),
    INSECTICIDA("Insecticida", 1),
    MASCARA("Máscara de gas", 2),
    LIBRO("Libro de supervivencia", 1),
    ESCOPETA("Escopeta", 3);

    private final String nombre;
    private final int tamano;

    TipoItem(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
    }

    public String getNombre() { return nombre; }
    public int getTamano() { return tamano; }

    /** El agua y la comida se pueden repartir de a media ración. */
    public boolean esRacion() { return this == AGUA || this == COMIDA; }
}

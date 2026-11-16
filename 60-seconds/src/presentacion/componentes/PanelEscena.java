package presentacion.componentes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JPanel;
import logica.Cargable;
import logica.Habitacion;
import logica.Item;
import logica.Persona;
import logica.Salida;

/**
 * Dibuja una habitación: el fondo, al protagonista, las salidas y todo lo que
 * se puede agarrar. En el refugio muestra además las provisiones guardadas.
 */
public class PanelEscena extends JPanel {

    // Dónde se para el protagonista en cada habitación (x de 0.0 a 1.0), lejos de puertas
    private static final Map<String, Double> POSICION_PROTAGONISTA = new HashMap<>();
    static {
        POSICION_PROTAGONISTA.put("REC", 0.33);
        POSICION_PROTAGONISTA.put("LIV", 0.12);
        POSICION_PROTAGONISTA.put("COC", 0.66);
        POSICION_PROTAGONISTA.put("GAR", 0.88);
        POSICION_PROTAGONISTA.put("PAS", 0.27);
        POSICION_PROTAGONISTA.put("DOR", 0.72);
        POSICION_PROTAGONISTA.put("CHI", 0.24);
        POSICION_PROTAGONISTA.put("BAN", 0.76);
        POSICION_PROTAGONISTA.put("REF", 0.38);
    }

    private Habitacion habitacion;
    private BufferedImage fondo;
    private Persona protagonista;
    private List<Item> provisiones; // solo en el refugio

    public PanelEscena() {
        super(null);
        setBackground(Color.BLACK);
    }

    public void mostrar(Habitacion habitacion, Persona protagonista, List<Item> provisiones,
                        Consumer<Cargable> alAgarrar, Consumer<Salida> alSalir) {
        removeAll();
        this.habitacion = habitacion;
        this.protagonista = protagonista;
        this.provisiones = provisiones;
        this.fondo = Recursos.imagenHabitacion(habitacion.getCodigo());

        // Swing dibuja arriba al primer componente agregado: primero lo que se agarra
        if (!habitacion.esRefugio()) {
            for (Persona p : habitacion.getPersonas()) agregar(new BotonCargable(p), alAgarrar);
            for (Item it : habitacion.getItemsDisponibles()) agregar(new BotonCargable(it), alAgarrar);
        }
        for (Salida s : habitacion.getSalidas()) {
            BotonSalida boton = new BotonSalida(s, getWidth(), getHeight());
            boton.addActionListener(e -> alSalir.accept(s));
            add(boton);
        }
        revalidate();
        repaint();
    }

    private void agregar(BotonCargable boton, Consumer<Cargable> alAgarrar) {
        boton.ubicarEn(getWidth(), getHeight());
        boton.addActionListener(e -> alAgarrar.accept(boton.getCargable()));
        add(boton);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (habitacion == null) return;
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int W = getWidth(), H = getHeight();
        g.drawImage(fondo, 0, 0, W, H, null);

        if (habitacion.esRefugio()) {
            pintarProvisiones(g, W, H);
            for (Persona p : habitacion.getPersonas()) pintarPersona(g, p, p.getPosX(), p.getPosY(), W, H);
        }
        Double x = POSICION_PROTAGONISTA.get(habitacion.getCodigo());
        if (protagonista != null && x != null) pintarPersona(g, protagonista, x, 0.99, W, H);

        // Cartel con el nombre de la habitación
        g.setFont(Estilo.TITULO);
        String titulo = habitacion.getNombre();
        int ancho = g.getFontMetrics().stringWidth(titulo) + 24;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, ancho, 34, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(titulo, 22, 34);

        g.setColor(Estilo.BORDE);
        g.drawRect(0, 0, W - 1, H - 1);
        g.dispose();
    }

    private void pintarPersona(Graphics2D g, Persona p, double px, double py, int W, int H) {
        int alto = (int) (H * 0.46);
        int ancho = alto / 2;
        int x = (int) (px * W) - ancho / 2;
        int y = (int) (py * H) - alto;
        g.drawImage(Recursos.imagenPersona(p), x, y, ancho, alto, null);
    }

    /** Las provisiones guardadas se ven en los estantes del refugio. */
    private void pintarProvisiones(Graphics2D g, int W, int H) {
        if (provisiones == null) return;
        double[] estantesX = {14 / 560.0, 362 / 560.0};
        double[] estantesY = {70 / 300.0, 130 / 300.0, 190 / 300.0};
        int tam = (int) (W * 0.045);
        int porFila = 6;
        int i = 0;
        for (Item it : provisiones) {
            int estante = i / (porFila * 3);
            if (estante > 1) break;
            int fila = (i / porFila) % 3;
            int col = i % porFila;
            int x = (int) (estantesX[estante] * W) + 8 + col * (tam + 2);
            int y = (int) (estantesY[fila] * H) - tam + 2;
            g.drawImage(Recursos.imagenItem(it.getTipo()), x, y, tam, tam, null);
            i++;
        }
    }
}

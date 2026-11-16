package presentacion.componentes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JComponent;
import logica.Cargable;
import logica.Item;
import logica.Mochila;
import logica.Persona;

/**
 * La mochila como una fila de espacios. Cada objeto ocupa tantos espacios
 * como su tamaño. Clic sobre un objeto para dejarlo.
 */
public class PanelMochila extends JComponent {
    private static final int SEPARACION = 6;

    private Mochila mochila;
    private Consumer<Cargable> alSoltar;
    private Cargable bajoElMouse;

    public PanelMochila() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) { actualizarHover(e.getX()); }
            @Override public void mouseExited(MouseEvent e) { actualizarHover(-1); }
            @Override public void mouseClicked(MouseEvent e) {
                Cargable c = cargableEn(e.getX());
                if (c != null && alSoltar != null) alSoltar.accept(c);
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void mostrar(Mochila mochila, Consumer<Cargable> alSoltar) {
        this.mochila = mochila;
        this.alSoltar = alSoltar;
        bajoElMouse = null;
        repaint();
    }

    private double anchoCelda() {
        return (getWidth() - SEPARACION * (Mochila.CAPACIDAD - 1)) / (double) Mochila.CAPACIDAD;
    }

    private Cargable cargableEn(int x) {
        if (mochila == null || x < 0) return null;
        int celda = (int) (x / (anchoCelda() + SEPARACION));
        int pos = 0;
        for (Cargable c : mochila.getContenido()) {
            if (celda >= pos && celda < pos + c.getTamano()) return c;
            pos += c.getTamano();
        }
        return null;
    }

    private void actualizarHover(int x) {
        Cargable c = cargableEn(x);
        if (c != bajoElMouse) {
            bajoElMouse = c;
            setCursor(Cursor.getPredefinedCursor(c != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            setToolTipText(c != null ? "Dejar " + c.getNombre() : null);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        double cw = anchoCelda();
        int h = getHeight();

        // Espacios vacíos
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{4, 4}, 0));
        for (int i = 0; i < Mochila.CAPACIDAD; i++) {
            int x = (int) (i * (cw + SEPARACION));
            g.setColor(new Color(40, 40, 45));
            g.fillRoundRect(x, 0, (int) cw, h - 1, 10, 10);
            g.setColor(Estilo.BORDE);
            g.drawRoundRect(x + 1, 1, (int) cw - 2, h - 3, 10, 10);
        }
        if (mochila == null) { g.dispose(); return; }

        // Objetos ocupando sus espacios
        int pos = 0;
        for (Cargable c : mochila.getContenido()) {
            int x = (int) (pos * (cw + SEPARACION));
            int w = (int) (c.getTamano() * cw + (c.getTamano() - 1) * SEPARACION);
            boolean hover = c == bajoElMouse;
            g.setStroke(new BasicStroke(2f));
            g.setColor(hover ? new Color(95, 45, 45) : new Color(70, 70, 80));
            g.fillRoundRect(x, 0, w, h - 1, 12, 12);
            g.setColor(hover ? Estilo.PELIGRO : new Color(130, 130, 140));
            g.drawRoundRect(x + 1, 1, w - 2, h - 3, 12, 12);

            int tam = h - 18;
            if (c instanceof Persona) {
                Persona p = (Persona) c;
                g.drawImage(Recursos.retrato(p), x + 10, 9, tam, tam, null);
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                g.setColor(Color.WHITE);
                g.drawString("Llevás a " + p.getNombre(), x + tam + 24, h / 2 - 2);
                g.setFont(Estilo.NORMAL);
                g.setColor(Estilo.TEXTO_SUAVE);
                g.drawString("Bajala al refugio y vaciá la mochila", x + tam + 24, h / 2 + 16);
            } else {
                Item it = (Item) c;
                g.drawImage(Recursos.imagenItem(it.getTipo()), x + (w - tam) / 2, 4, tam, tam, null);
                g.setFont(Estilo.CHICA);
                FontMetrics fm = g.getFontMetrics();
                String nombre = it.getNombre();
                if (fm.stringWidth(nombre) > w - 6) nombre = nombre.split(" ")[0];
                g.setColor(Color.WHITE);
                g.drawString(nombre, x + (w - fm.stringWidth(nombre)) / 2, h - 6);
            }
            if (hover) {
                g.setColor(Estilo.PELIGRO);
                g.fillOval(x + w - 20, 3, 17, 17);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(2f));
                g.drawLine(x + w - 16, 7, x + w - 7, 16);
                g.drawLine(x + w - 7, 7, x + w - 16, 16);
            }
            pos += c.getTamano();
        }
        g.dispose();
    }
}

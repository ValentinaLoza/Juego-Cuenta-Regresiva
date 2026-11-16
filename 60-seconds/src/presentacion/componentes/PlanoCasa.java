package presentacion.componentes;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import logica.Habitacion;
import logica.Persona;

/**
 * Minimapa de la casa. Marca dónde está el protagonista y, en las
 * habitaciones que ya visitó, si quedan familiares u objetos.
 */
public class PlanoCasa extends JComponent {
    private static final int COLUMNAS = 4, FILAS = 4;

    private List<Habitacion> habitaciones;
    private Habitacion actual;
    private final Set<Habitacion> visitadas = new HashSet<>();

    public void mostrar(List<Habitacion> habitaciones, Habitacion actual) {
        this.habitaciones = habitaciones;
        this.actual = actual;
        visitadas.add(actual);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        if (habitaciones == null) return;
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int W = getWidth(), H = getHeight();
        g.setColor(Estilo.FONDO_PANEL);
        g.fillRoundRect(0, 0, W, H, 12, 12);
        g.setFont(Estilo.CHICA);
        g.setColor(Estilo.TEXTO_SUAVE);
        g.drawString("PLANO DE LA CASA", 10, 16);

        int margen = 8, top = 24;
        double cw = (W - 2 * margen) / (double) COLUMNAS;
        double ch = (H - top - margen) / (double) FILAS;

        for (Habitacion h : habitaciones) {
            int x = (int) (margen + h.getPlanoX() * cw) + 2;
            int y = (int) (top + h.getPlanoY() * ch) + 2;
            int w = (int) (h.getPlanoAncho() * cw) - 4;
            int alto = (int) ch - 4;
            boolean esActual = h == actual;
            Color relleno = h.esRefugio() ? new Color(60, 85, 60) : new Color(70, 70, 78);
            if (esActual) relleno = new Color(120, 100, 40);
            g.setColor(relleno);
            g.fillRoundRect(x, y, w, alto, 8, 8);
            g.setColor(esActual ? Estilo.ACENTO : Estilo.BORDE);
            g.setStroke(new BasicStroke(esActual ? 2f : 1f));
            g.drawRoundRect(x, y, w, alto, 8, 8);

            String nombre = abreviar(h.getNombre());
            g.setFont(Estilo.CHICA);
            g.setColor(Color.WHITE);
            g.drawString(nombre, x + (w - g.getFontMetrics().stringWidth(nombre)) / 2, y + 13);

            // Lo que sabemos de esta habitación (solo si ya la visitamos)
            if (visitadas.contains(h) && !h.esRefugio()) {
                int ix = x + 4;
                for (Persona p : h.getPersonas()) {
                    g.drawImage(Recursos.retrato(p), ix, y + alto - 17, 15, 15, null);
                    ix += 16;
                }
                int objetos = h.getItemsDisponibles().size();
                if (objetos > 0) {
                    String n = objetos + "";
                    g.setColor(Estilo.ACENTO);
                    g.fillOval(x + w - 17, y + alto - 17, 14, 14);
                    g.setColor(Color.BLACK);
                    g.drawString(n, x + w - 10 - g.getFontMetrics().stringWidth(n) / 2, y + alto - 6);
                }
            }
            if (esActual) { // el protagonista
                g.setColor(Color.WHITE);
                g.fillOval(x + w / 2 - 5, y + alto / 2 + 1, 10, 10);
                g.setColor(Color.BLACK);
                g.drawOval(x + w / 2 - 5, y + alto / 2 + 1, 10, 10);
            }
        }
        g.dispose();
    }

    private String abreviar(String nombre) {
        switch (nombre) {
            case "Cuarto de los chicos": return "Chicos";
            case "Dormitorio": return "Dormit.";
            case "Recibidor": return "Recib.";
            default: return nombre;
        }
    }
}

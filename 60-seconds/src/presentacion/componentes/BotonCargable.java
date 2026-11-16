package presentacion.componentes;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import logica.Cargable;
import logica.Item;
import logica.Persona;

/**
 * Algo que se puede agarrar dentro de una habitación: un objeto o un familiar.
 * Al pasar el mouse brilla; al hacer clic va a la mochila.
 */
public class BotonCargable extends JButton {
    private static final int TAM_ICONO = 64;
    private static final int ALTO_PERSONA = 175;

    private final Cargable cargable;
    private final BufferedImage imagen;
    private final String etiqueta;
    private boolean encima;

    public BotonCargable(Cargable cargable) {
        this.cargable = cargable;
        if (cargable instanceof Persona) {
            Persona p = (Persona) cargable;
            imagen = Recursos.escalar(Recursos.imagenPersona(p), ALTO_PERSONA / 2, ALTO_PERSONA);
            etiqueta = "Llevar a " + p.getNombre();
            setSize(124, ALTO_PERSONA + 6);
        } else {
            Item it = (Item) cargable;
            imagen = Recursos.escalar(Recursos.imagenItem(it.getTipo()), TAM_ICONO + 8, TAM_ICONO + 8);
            etiqueta = it.getNombre();
            setSize(116, 92);
        }
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(etiqueta + "  (ocupa " + cargable.getTamano() + " espacios)");
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { encima = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { encima = false; repaint(); }
        });
    }

    public Cargable getCargable() {
        return cargable;
    }

    /** Ubica el botón para que la cosa quede "apoyada" en su posición (posX, posY) de la escena. */
    public void ubicarEn(int anchoEscena, int altoEscena) {
        double px, py;
        int pies; // a qué altura del botón están los "pies" del dibujo
        if (cargable instanceof Persona) {
            px = ((Persona) cargable).getPosX();
            py = ((Persona) cargable).getPosY();
            pies = ALTO_PERSONA;
        } else {
            px = ((Item) cargable).getPosX();
            py = ((Item) cargable).getPosY();
            pies = 78;
        }
        int x = (int) (px * anchoEscena) - getWidth() / 2;
        int y = (int) (py * altoEscena) - pies;
        x = Math.max(0, Math.min(anchoEscena - getWidth(), x));
        y = Math.max(0, Math.min(altoEscena - getHeight(), y));
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int cx = getWidth() / 2;
        boolean esPersona = cargable instanceof Persona;

        if (encima) {
            int r = esPersona ? 80 : 42;
            int cy = esPersona ? ALTO_PERSONA / 2 : 38;
            g.setPaint(new RadialGradientPaint(cx, cy, r, new float[]{0f, 1f},
                    new Color[]{new Color(255, 230, 120, 170), new Color(255, 230, 120, 0)}));
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
        }
        int yEtiqueta;
        if (esPersona) {
            int w = imagen.getWidth(), h = imagen.getHeight();
            if (encima) g.drawImage(imagen, cx - w / 2 - 3, -2, w + 6, h + 4, null);
            else g.drawImage(imagen, cx - w / 2, 2, null);
            yEtiqueta = ALTO_PERSONA - 34;
        } else {
            int tam = encima ? TAM_ICONO + 8 : TAM_ICONO;
            g.drawImage(imagen, cx - tam / 2, encima ? 0 : 6, tam, tam, null);
            yEtiqueta = getHeight() - 20;
        }

        g.setFont(Estilo.CHICA);
        FontMetrics fm = g.getFontMetrics();
        int anchoTexto = fm.stringWidth(etiqueta) + 12;
        g.setColor(encima ? new Color(255, 200, 70, 235) : new Color(0, 0, 0, 170));
        g.fill(new RoundRectangle2D.Double(cx - anchoTexto / 2.0, yEtiqueta, anchoTexto, 18, 18, 18));
        g.setColor(encima ? Color.BLACK : Color.WHITE);
        g.drawString(etiqueta, cx - fm.stringWidth(etiqueta) / 2, yEtiqueta + 13);
        g.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        int margen = cargable instanceof Persona ? 22 : 14;
        return x > margen && x < getWidth() - margen && y > 0 && y < getHeight();
    }
}

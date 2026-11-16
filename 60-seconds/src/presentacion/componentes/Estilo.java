package presentacion.componentes;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Colores, fuentes y botones compartidos por toda la interfaz. */
public final class Estilo {
    private Estilo() { }

    public static final int ANCHO = 960;
    public static final int ALTO = 640;

    public static final Color FONDO = new Color(30, 30, 34);
    public static final Color FONDO_PANEL = new Color(48, 48, 54);
    public static final Color BORDE = new Color(80, 80, 88);
    public static final Color TEXTO = new Color(235, 235, 235);
    public static final Color TEXTO_SUAVE = new Color(160, 160, 170);
    public static final Color ACENTO = new Color(255, 200, 70);
    public static final Color PELIGRO = new Color(230, 70, 60);
    public static final Color EXITO = new Color(110, 210, 110);
    public static final Color AGUA = new Color(90, 170, 240);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font NEGRITA = new Font("SansSerif", Font.BOLD, 13);
    public static final Font CHICA = new Font("SansSerif", Font.BOLD, 11);

    /** Botón plano con el estilo del juego. */
    public static JButton boton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.darker(), 2),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return b;
    }

    public static Color colorSegunValor(int valor) {
        if (valor > 50) return EXITO;
        if (valor > 25) return ACENTO;
        return PELIGRO;
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
}

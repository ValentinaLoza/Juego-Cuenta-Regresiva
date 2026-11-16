package presentacion.componentes;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** Panel que dibuja una imagen de fondo, oscurecida (0 = nada, 255 = negro). */
public class PanelConFondo extends JPanel {
    private final BufferedImage fondo;
    private final int oscuridad;

    public PanelConFondo(BufferedImage fondo, int oscuridad) {
        super(null);
        this.fondo = fondo;
        this.oscuridad = oscuridad;
        setBackground(Estilo.FONDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Estilo.suavizar(g2);
        g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        g2.setColor(new Color(15, 15, 18, oscuridad));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}

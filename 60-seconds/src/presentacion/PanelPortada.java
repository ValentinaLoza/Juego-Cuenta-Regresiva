package presentacion;

import java.awt.*;
import javax.swing.*;
import presentacion.componentes.Estilo;
import presentacion.componentes.PanelConFondo;
import presentacion.componentes.Recursos;

/** Portada del juego. */
public class PanelPortada extends PanelConFondo {

    public PanelPortada(FrmPrincipal ventana) {
        super(Recursos.portada(), 0);

        JLabel lblTitulo = new JLabel("CUENTA REGRESIVA", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                // sombra del título
                Graphics2D g2 = (Graphics2D) g.create();
                Estilo.suavizar(g2);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString(getText(), x + 4, fm.getAscent() + 4);
                g2.setColor(getForeground());
                g2.drawString(getText(), x, fm.getAscent());
                g2.dispose();
            }
        };
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 64));
        lblTitulo.setForeground(new Color(255, 225, 150));
        lblTitulo.setBounds(0, 60, Estilo.ANCHO, 80);
        add(lblTitulo);

        JLabel lblSub = new JLabel("60 segundos para salvar a tu familia", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSub.setForeground(Color.WHITE);
        lblSub.setBounds(0, 140, Estilo.ANCHO, 30);
        add(lblSub);

        JButton btnJugar = Estilo.boton("JUGAR", Estilo.PELIGRO, Color.WHITE);
        btnJugar.setFont(new Font("SansSerif", Font.BOLD, 24));
        btnJugar.setBounds(Estilo.ANCHO / 2 - 120, 540, 240, 60);
        btnJugar.addActionListener(e -> ventana.mostrarAdvertencias());
        add(btnJugar);

        JButton btnHistorial = Estilo.boton("Historial", new Color(60, 40, 45), Color.WHITE);
        btnHistorial.setBounds(20, 580, 140, 40);
        btnHistorial.addActionListener(e -> ventana.mostrarHistorial());
        add(btnHistorial);

        JButton btnSalir = Estilo.boton("Salir", new Color(60, 40, 45), Color.WHITE);
        btnSalir.setBounds(Estilo.ANCHO - 140, 580, 120, 40);
        btnSalir.addActionListener(e -> ventana.salir());
        add(btnSalir);
    }
}

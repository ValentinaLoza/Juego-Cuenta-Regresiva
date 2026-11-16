package presentacion.componentes;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.JButton;
import logica.Salida;
import logica.TipoSalida;

/**
 * Una puerta, arcada, escotilla o escalera dibujada en la escena, con un
 * cartel y una flecha que indica a dónde lleva.
 */
public class BotonSalida extends JButton {
    private static final double HORIZONTE = 200.0 / 300.0; // donde termina la pared en las imágenes
    private static final int ALTO_CARTEL = 24;

    private final Salida salida;
    private final String texto;
    private boolean encima;
    private int anchoAbertura, altoAbertura;

    public BotonSalida(Salida salida, int anchoEscena, int altoEscena) {
        this.salida = salida;
        this.texto = salida.getDestino().getNombre();
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Ir a " + texto);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { encima = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { encima = false; repaint(); }
        });

        FontMetrics fm = getFontMetrics(Estilo.NEGRITA);
        int anchoCartel = fm.stringWidth(texto) + 40;
        int cx = (int) (salida.getPosX() * anchoEscena);
        int horizonte = (int) (HORIZONTE * altoEscena);

        switch (salida.getTipo()) {
            case ESCOTILLA:
                anchoAbertura = (int) (anchoEscena * 0.22);
                altoAbertura = (int) (altoEscena * 0.13);
                int ancho = Math.max(anchoAbertura, anchoCartel) + 6;
                setBounds(cx - ancho / 2, (int) (altoEscena * 0.86) - altoAbertura / 2 - ALTO_CARTEL - 6,
                        ancho, ALTO_CARTEL + 6 + altoAbertura);
                break;
            case ESCALERA:
                anchoAbertura = (int) (anchoEscena * 0.11);
                altoAbertura = (int) (altoEscena * 0.93);
                ancho = Math.max(anchoAbertura, anchoCartel) + 6;
                setBounds(cx - ancho / 2, 0, ancho, altoEscena);
                break;
            default: // PUERTA o ARCADA
                anchoAbertura = (int) (anchoEscena * 0.13);
                altoAbertura = (int) (altoEscena * 0.42);
                ancho = Math.max(anchoAbertura, anchoCartel) + 6;
                setBounds(cx - ancho / 2, horizonte - altoAbertura - ALTO_CARTEL - 6, ancho, ALTO_CARTEL + 6 + altoAbertura);
        }
    }

    public Salida getSalida() {
        return salida;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth();
        int x0 = (w - anchoAbertura) / 2;
        TipoSalida tipo = salida.getTipo();

        if (tipo == TipoSalida.ESCALERA) {
            pintarEscalera(g, x0, (getHeight() - altoAbertura) / 2);
            pintarCartel(g, w, getHeight() / 2 - 40, false);
        } else if (tipo == TipoSalida.ESCOTILLA) {
            pintarEscotilla(g, x0, ALTO_CARTEL + 6);
            pintarCartel(g, w, 0, true);
        } else {
            int y0 = ALTO_CARTEL + 6;
            if (tipo == TipoSalida.ARCADA) pintarArcada(g, x0, y0);
            else pintarPuerta(g, x0, y0);
            pintarCartel(g, w, 0, false);
        }
        g.dispose();
    }

    private void pintarPuerta(Graphics2D g, int x, int y) {
        int w = anchoAbertura, h = altoAbertura;
        // marco
        g.setColor(new Color(235, 225, 205));
        g.fillRect(x - 5, y - 5, w + 10, h + 5);
        // interior oscuro (se ve al abrir)
        g.setPaint(new GradientPaint(0, y, new Color(20, 15, 15), 0, y + h, new Color(60, 45, 35)));
        g.fillRect(x, y, w, h);
        // hoja de la puerta: entreabierta cuando el mouse está encima
        int hoja = encima ? (int) (w * 0.62) : w;
        Color madera = encima ? new Color(175, 115, 65) : new Color(150, 95, 55);
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x + hoja, y + (encima ? 6 : 0));
        p.addPoint(x + hoja, y + h - (encima ? 6 : 0));
        p.addPoint(x, y + h);
        g.setColor(madera);
        g.fillPolygon(p);
        g.setColor(madera.darker());
        g.setStroke(new BasicStroke(2f));
        g.drawPolygon(p);
        // paneles
        int m = hoja / 6;
        g.drawRect(x + m, y + h / 10, hoja - 2 * m, h * 3 / 10);
        g.drawRect(x + m, y + h / 2, hoja - 2 * m, h * 4 / 10 - 4);
        // picaporte
        g.setColor(new Color(230, 200, 90));
        g.fillOval(x + hoja - 12, y + h / 2, 7, 7);
    }

    private void pintarArcada(Graphics2D g, int x, int y) {
        int w = anchoAbertura, h = altoAbertura;
        Area arco = new Area(new Rectangle2D.Double(x, y + w / 2.0, w, h - w / 2.0));
        arco.add(new Area(new Ellipse2D.Double(x, y, w, w)));
        g.setPaint(new GradientPaint(0, y, new Color(60, 45, 35), 0, y + h,
                encima ? new Color(150, 120, 90) : new Color(110, 85, 60)));
        g.fill(arco);
        g.setColor(new Color(240, 232, 215));
        g.setStroke(new BasicStroke(7f));
        g.draw(arco);
    }

    private void pintarEscotilla(Graphics2D g, int x, int y) {
        int w = anchoAbertura, h = altoAbertura;
        Shape tapa = new Ellipse2D.Double(x, y, w, h);
        g.setPaint(new GradientPaint(0, y, encima ? new Color(140, 150, 140) : new Color(105, 112, 105),
                0, y + h, new Color(55, 60, 55)));
        g.fill(tapa);
        g.setColor(new Color(35, 38, 35));
        g.setStroke(new BasicStroke(4f));
        g.draw(tapa);
        g.draw(new Ellipse2D.Double(x + w * 0.3, y + h * 0.25, w * 0.4, h * 0.5));
        g.setStroke(new BasicStroke(3f));
        g.draw(new Line2D.Double(x + w / 2.0, y + h * 0.25, x + w / 2.0, y + h * 0.75));
        g.draw(new Line2D.Double(x + w * 0.3, y + h / 2.0, x + w * 0.7, y + h / 2.0));
        if (encima) {
            g.setColor(new Color(255, 200, 70, 90));
            g.fill(tapa);
        }
    }

    private void pintarEscalera(Graphics2D g, int x, int y) {
        int w = anchoAbertura, h = altoAbertura;
        Color metal = encima ? new Color(200, 200, 190) : new Color(150, 150, 140);
        g.setColor(metal);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + 6, y, x + 6, y + h);
        g.drawLine(x + w - 6, y, x + w - 6, y + h);
        g.setStroke(new BasicStroke(4f));
        for (int yy = y + 20; yy < y + h; yy += 26) g.drawLine(x + 6, yy, x + w - 6, yy);
        // luz que entra por la escotilla de arriba
        g.setPaint(new GradientPaint(0, 0, new Color(255, 230, 160, encima ? 160 : 90), 0, 80, new Color(255, 230, 160, 0)));
        g.fillRect(x - 10, 0, w + 20, 80);
    }

    /** Cartel con flecha: "↑ Cocina" (o ↓ para la escotilla). */
    private void pintarCartel(Graphics2D g, int anchoTotal, int y, boolean flechaAbajo) {
        g.setFont(Estilo.NEGRITA);
        FontMetrics fm = g.getFontMetrics();
        int ancho = fm.stringWidth(texto) + 34;
        int x = (anchoTotal - ancho) / 2;
        g.setColor(encima ? Estilo.ACENTO : new Color(20, 20, 24, 210));
        g.fill(new RoundRectangle2D.Double(x, y, ancho, ALTO_CARTEL, 12, 12));
        // flecha
        int fx = x + 14, fy = y + ALTO_CARTEL / 2;
        Polygon flecha = new Polygon();
        if (flechaAbajo) {
            flecha.addPoint(fx - 6, fy - 3); flecha.addPoint(fx + 6, fy - 3); flecha.addPoint(fx, fy + 6);
        } else {
            flecha.addPoint(fx - 6, fy + 3); flecha.addPoint(fx + 6, fy + 3); flecha.addPoint(fx, fy - 6);
        }
        g.setColor(encima ? Color.BLACK : Estilo.ACENTO);
        g.fillPolygon(flecha);
        g.setColor(encima ? Color.BLACK : Color.WHITE);
        g.drawString(texto, x + 26, y + 17);
    }

    @Override
    public boolean contains(int x, int y) {
        // Solo la abertura y el cartel, no los costados vacíos
        int x0 = (getWidth() - anchoAbertura) / 2;
        boolean enAbertura = x >= x0 - 4 && x <= x0 + anchoAbertura + 4;
        boolean enCartel = salida.getTipo() == TipoSalida.ESCALERA
                ? (y >= getHeight() / 2 - 40 && y <= getHeight() / 2 - 40 + ALTO_CARTEL)
                : y <= ALTO_CARTEL;
        return (enAbertura || enCartel) && y >= 0 && y < getHeight();
    }
}

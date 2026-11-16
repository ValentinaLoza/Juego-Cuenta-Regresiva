package presentacion.componentes;

import java.awt.*;
import javax.swing.*;
import logica.Persona;
import logica.TipoItem;

/**
 * Tarjeta de un familiar en el refugio: retrato, estado, barras de salud,
 * hidratación y alimentación, y los botones para repartirle raciones
 * (cada clic cambia: nada → ½ → 1) o curarlo.
 *
 * No decide nada: avisa los clics y la lógica resuelve si se puede.
 */
public class TarjetaFamiliar extends JPanel {
    private static final Color GRIS_BOTON = new Color(70, 70, 80);
    private static final Color COMIDA = new Color(220, 120, 60);

    private final Persona persona;
    private final JLabel lblRetrato = new JLabel();
    private final JLabel lblNombre = new JLabel();
    private final JLabel lblEstado = new JLabel();
    private final Barra barSalud = new Barra("Salud");
    private final Barra barAgua = new Barra("Agua");
    private final Barra barComida = new Barra("Comida");
    private final JButton btnAgua;
    private final JButton btnComida;
    private final JButton btnCurar;

    public TarjetaFamiliar(Persona persona, Runnable alClicAgua, Runnable alClicComida, Runnable alCurar) {
        super(null);
        this.persona = persona;
        setBackground(Estilo.FONDO_PANEL);
        setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        lblRetrato.setBounds(8, 8, 52, 52);
        add(lblRetrato);

        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNombre.setBounds(68, 4, 160, 18);
        add(lblNombre);
        lblEstado.setFont(Estilo.CHICA);
        lblEstado.setBounds(68, 20, 160, 16);
        add(lblEstado);

        barSalud.setBounds(68, 38, 160, 9);
        barAgua.setBounds(68, 51, 160, 7);
        barComida.setBounds(68, 62, 160, 7);
        add(barSalud);
        add(barAgua);
        add(barComida);

        btnAgua = crearBoton(TipoItem.AGUA, 8, 70);
        btnComida = crearBoton(TipoItem.COMIDA, 82, 76);
        btnAgua.addActionListener(e -> alClicAgua.run());
        btnComida.addActionListener(e -> alClicComida.run());

        btnCurar = new JButton("Curar", Recursos.iconoItem(TipoItem.MEDICAMENTO, 18));
        estilizar(btnCurar);
        btnCurar.setToolTipText("Usar un botiquín (+30 salud)");
        btnCurar.setBackground(new Color(150, 50, 50));
        btnCurar.setBounds(162, 76, 66, 30);
        btnCurar.addActionListener(e -> alCurar.run());
        add(btnCurar);
    }

    private JButton crearBoton(TipoItem tipo, int x, int ancho) {
        JButton b = new JButton(Recursos.iconoItem(tipo, 18));
        estilizar(b);
        b.setToolTipText("Clic para cambiar la ración de " + tipo.getNombre().toLowerCase() + ": nada → ½ → 1");
        b.setBounds(x, 76, ancho, 30);
        add(b);
        return b;
    }

    private void estilizar(JButton b) {
        b.setFont(Estilo.CHICA);
        b.setForeground(Color.WHITE);
        b.setMargin(new Insets(1, 1, 1, 1));
        b.setIconTextGap(3);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Persona getPersona() {
        return persona;
    }

    /**
     * Refresca la tarjeta.
     * @param modoNoche  true si el jugador está repartiendo raciones
     * @param hayAgua    true si se le puede cambiar la ración de agua
     * @param hayComida  true si se le puede cambiar la ración de comida
     * @param hayBotiquin true si hay botiquines guardados
     */
    public void actualizar(boolean modoNoche, boolean hayAgua, boolean hayComida, boolean hayBotiquin) {
        boolean viva = persona.estaViva();
        Image retrato = viva ? Recursos.retrato(persona) : Recursos.grises(Recursos.retrato(persona));
        lblRetrato.setIcon(new ImageIcon(Recursos.escalar(retrato, 52, 52)));
        lblNombre.setText(viva ? persona.getNombre() : "✝ " + persona.getNombre());
        lblNombre.setForeground(viva ? Color.WHITE : Estilo.TEXTO_SUAVE);

        String estado = persona.getEstado();
        lblEstado.setText(viva ? estado + " · salud " + persona.getSalud() + "%" : estado);
        lblEstado.setForeground(!viva ? Estilo.TEXTO_SUAVE : estado.equals("Bien") ? Estilo.EXITO
                : estado.startsWith("Con") ? Estilo.ACENTO : Estilo.PELIGRO);

        barSalud.mostrar(persona.getSalud(), Estilo.colorSegunValor(persona.getSalud()));
        barAgua.mostrar(persona.getHidratacion(), Estilo.AGUA);
        barComida.mostrar(persona.getAlimentacion(), COMIDA);

        configurarRacion(btnAgua, persona.getRacionAgua(), viva && modoNoche && hayAgua, Estilo.AGUA);
        configurarRacion(btnComida, persona.getRacionComida(), viva && modoNoche && hayComida, COMIDA);
        btnCurar.setEnabled(viva && hayBotiquin && persona.getSalud() < 100);
        setBackground(viva ? Estilo.FONDO_PANEL : new Color(38, 38, 42));
    }

    private void configurarRacion(JButton b, int mitades, boolean habilitado, Color color) {
        b.setText(mitades == 0 ? "—" : mitades == 1 ? "½" : "1");
        b.setFont(new Font("SansSerif", Font.BOLD, mitades == 0 ? 12 : 15));
        b.setEnabled(habilitado);
        b.setBackground(mitades == 0 ? GRIS_BOTON : mitades == 1 ? color.darker() : color);
    }

    /** Barra chiquita con un nombre (salud, agua, comida). */
    private static class Barra extends JComponent {
        private final String nombre;
        private int valor;
        private Color color = Color.GRAY;

        Barra(String nombre) {
            this.nombre = nombre;
        }

        void mostrar(int valor, Color color) {
            this.valor = valor;
            this.color = color;
            setToolTipText(nombre + ": " + valor + "%");
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Estilo.suavizar(g);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(30, 30, 34));
            g.fillRoundRect(0, 0, w, h, h, h);
            g.setColor(color);
            g.fillRoundRect(0, 0, (int) (w * valor / 100.0), h, h, h);
            g.dispose();
        }
    }
}

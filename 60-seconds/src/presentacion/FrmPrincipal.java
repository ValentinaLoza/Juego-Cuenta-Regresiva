package presentacion;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import logica.GestorPartida;
import logica.Persona;
import logica.RepositorioEventos;
import logica.ServicioHistorial;
import presentacion.componentes.Estilo;

/**
 * Ventana principal. Solo coordina qué pantalla se ve y el menú:
 * portada → advertencias → casa (fase 1) → refugio (fase 2) → final,
 * más el historial de partidas.
 *
 * Recibe por constructor lo que necesita de las otras capas (el catálogo de
 * eventos y el servicio de historial): no crea ni conoce los ArrayList.
 */
public class FrmPrincipal extends JFrame {

    private final JPanel pnlContenedor = new JPanel(new CardLayout());
    private final RepositorioEventos repositorioEventos;
    private final ServicioHistorial historial;
    private GestorPartida gestor;

    public FrmPrincipal(RepositorioEventos repositorioEventos, ServicioHistorial historial) {
        this.repositorioEventos = repositorioEventos;
        this.historial = historial;
        setTitle("Cuenta Regresiva - Supervivencia");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });
        setResizable(false);
        ToolTipManager.sharedInstance().setInitialDelay(300);
        setJMenuBar(crearMenu());

        pnlContenedor.setPreferredSize(new Dimension(Estilo.ANCHO, Estilo.ALTO));
        add(pnlContenedor);
        pack();
        setLocationRelativeTo(null);
        mostrarPortada();
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu mnuJuego = new JMenu("Juego");
        JMenuItem itmNueva = new JMenuItem("Nueva partida");
        itmNueva.addActionListener(e -> { if (confirmarAbandono()) mostrarAdvertencias(); });
        JMenuItem itmHistorial = new JMenuItem("Historial de partidas");
        itmHistorial.addActionListener(e -> { if (confirmarAbandono()) mostrarHistorial(); });
        JMenuItem itmPortada = new JMenuItem("Volver a la portada");
        itmPortada.addActionListener(e -> { if (confirmarAbandono()) mostrarPortada(); });
        JMenuItem itmSalir = new JMenuItem("Salir");
        itmSalir.addActionListener(e -> salir());
        mnuJuego.add(itmNueva);
        mnuJuego.add(itmHistorial);
        mnuJuego.add(itmPortada);
        mnuJuego.addSeparator();
        mnuJuego.add(itmSalir);

        JMenu mnuAyuda = new JMenu("Ayuda");
        JMenuItem itmComo = new JMenuItem("Cómo jugar");
        itmComo.addActionListener(e -> { if (confirmarAbandono()) mostrarAdvertencias(); });
        JMenuItem itmAcerca = new JMenuItem("Acerca de");
        itmAcerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Cuenta Regresiva\nTrabajo Práctico Integrador\nDesarrollo de Sistemas Orientados a Objetos",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        mnuAyuda.add(itmComo);
        mnuAyuda.add(itmAcerca);

        barra.add(mnuJuego);
        barra.add(mnuAyuda);
        return barra;
    }

    /** Si hay una partida en curso, pregunta antes de abandonarla. */
    private boolean confirmarAbandono() {
        Component actual = pnlContenedor.getComponentCount() > 0 ? pnlContenedor.getComponent(0) : null;
        boolean jugando = actual instanceof PanelRecoleccion || actual instanceof PanelRefugio;
        if (!jugando) return true;
        int r = JOptionPane.showConfirmDialog(this, "Hay una partida en curso. ¿Abandonarla?",
                "Abandonar partida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION && actual instanceof PanelRecoleccion) {
            ((PanelRecoleccion) actual).detener();
        }
        return r == JOptionPane.YES_OPTION;
    }

    public void salir() {
        if (confirmarAbandono()) System.exit(0);
    }

    private void mostrar(JPanel pantalla) {
        pnlContenedor.removeAll();
        pnlContenedor.add(pantalla);
        pnlContenedor.revalidate();
        pnlContenedor.repaint();
    }

    public void mostrarPortada() {
        mostrar(new PanelPortada(this));
    }

    public void mostrarAdvertencias() {
        mostrar(new PanelAdvertencias(this));
    }

    public void mostrarHistorial() {
        mostrar(new PanelHistorial(this, historial));
    }

    public void nuevaPartida() {
        gestor = new GestorPartida(repositorioEventos);
        PanelRecoleccion panel = new PanelRecoleccion(this, gestor);
        mostrar(panel);
        panel.comenzar();
    }

    /** Termina la fase 1. Si el protagonista no llegó al refugio, la partida termina. */
    public void terminarRecoleccion() {
        if (!gestor.terminarRecoleccion()) {
            mostrarFinal(false, "NO LLEGASTE",
                    "La onda expansiva te alcanzó antes de que pudieras bajar al refugio.\n"
                  + "La próxima vez, mirá el reloj y volvé a tiempo.", null);
            return;
        }
        mostrar(new PanelRefugio(this, gestor));
    }

    public void mostrarFinal(boolean victoria, String titulo, String texto, List<Persona> familia) {
        mostrar(new PanelFinal(this, historial, gestor, victoria, titulo, texto, familia));
    }
}

package presentacion;

import java.awt.*;
import javax.swing.*;
import logica.AccionInvalidaException;
import logica.Cargable;
import logica.GestorPartida;
import logica.Mochila;
import logica.Persona;
import logica.Salida;
import presentacion.componentes.Estilo;
import presentacion.componentes.PanelEscena;
import presentacion.componentes.PanelMochila;
import presentacion.componentes.PlanoCasa;
import presentacion.componentes.Recursos;

/** Fase 1: recorrer la casa juntando provisiones y a la familia. */
public class PanelRecoleccion extends JPanel {

    private final FrmPrincipal ventana;
    private final GestorPartida gestor;
    private Timer timerJuego;
    private Timer timerMensaje;
    private boolean terminado;

    private final JProgressBar barTiempo = new JProgressBar(0, GestorPartida.TIEMPO_RECOLECCION);
    private final PanelEscena pnlEscena = new PanelEscena();
    private final PlanoCasa plano = new PlanoCasa();
    private final PanelMochila pnlMochila = new PanelMochila();
    private final JLabel lblMochila = new JLabel();
    private final JLabel lblMensaje = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblAviso = new JLabel("", SwingConstants.CENTER);
    private final JLabel[] lblsFamilia;
    private final JButton btnVaciar = Estilo.boton("Vaciar la mochila", Estilo.EXITO.darker(), Color.WHITE);
    private final JButton btnCerrar = Estilo.boton("Cerrar la escotilla", Estilo.PELIGRO, Color.WHITE);
    private final JLabel lblAyudaRefugio = new JLabel();

    public PanelRecoleccion(FrmPrincipal ventana, GestorPartida gestor) {
        super(null);
        this.ventana = ventana;
        this.gestor = gestor;
        setBackground(Estilo.FONDO);

        // --- Barra superior ---
        JLabel lblTitulo = new JLabel("¡RÁPIDO!");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(Estilo.ACENTO);
        lblTitulo.setBounds(20, 14, 160, 36);
        add(lblTitulo);

        barTiempo.setValue(GestorPartida.TIEMPO_RECOLECCION);
        barTiempo.setStringPainted(true);
        barTiempo.setFont(new Font("SansSerif", Font.BOLD, 16));
        barTiempo.setForeground(Estilo.EXITO);
        barTiempo.setBackground(new Color(55, 55, 60));
        barTiempo.setBorderPainted(false);
        barTiempo.setBounds(170, 18, 550, 30);
        add(barTiempo);

        lblAviso.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblAviso.setOpaque(true);
        lblAviso.setBackground(Estilo.PELIGRO);
        lblAviso.setForeground(Color.WHITE);
        lblAviso.setBounds(735, 14, 205, 38);
        lblAviso.setVisible(false);
        add(lblAviso);

        // --- Escena ---
        pnlEscena.setBounds(20, 66, 700, 375);
        add(pnlEscena);

        // --- Columna derecha: plano, familia, mensajes, refugio ---
        plano.setBounds(735, 66, 205, 190);
        add(plano);

        JPanel pnlFamilia = new JPanel(null);
        pnlFamilia.setBackground(Estilo.FONDO_PANEL);
        pnlFamilia.setBounds(735, 266, 205, 112);
        JLabel lblFam = new JLabel("FAMILIA");
        lblFam.setFont(Estilo.CHICA);
        lblFam.setForeground(Estilo.TEXTO_SUAVE);
        lblFam.setBounds(10, 4, 100, 16);
        pnlFamilia.add(lblFam);
        lblsFamilia = new JLabel[gestor.getFamilia().size()];
        for (int i = 0; i < lblsFamilia.length; i++) {
            Persona p = gestor.getFamilia().get(i);
            JLabel lbl = new JLabel(p.getNombre(), Recursos.iconoRetrato(p, 26), SwingConstants.LEFT);
            lbl.setFont(Estilo.NEGRITA);
            lbl.setIconTextGap(8);
            lbl.setBounds(10, 22 + i * 29, 190, 28);
            lblsFamilia[i] = lbl;
            pnlFamilia.add(lbl);
        }
        add(pnlFamilia);

        lblMensaje.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMensaje.setBounds(735, 384, 205, 58);
        add(lblMensaje);

        btnVaciar.setBounds(735, 452, 205, 48);
        btnVaciar.addActionListener(e -> vaciar());
        add(btnVaciar);

        btnCerrar.setBounds(735, 508, 205, 48);
        btnCerrar.addActionListener(e -> cerrarEscotilla());
        add(btnCerrar);

        lblAyudaRefugio.setText("<html><div style='color:#a0a0aa'>Para vaciar la mochila bajá al "
                + "<b style='color:#ffc846'>refugio</b> por la escotilla del recibidor.</div></html>");
        lblAyudaRefugio.setFont(Estilo.NORMAL);
        lblAyudaRefugio.setBounds(740, 452, 200, 100);
        add(lblAyudaRefugio);

        // --- Mochila ---
        lblMochila.setFont(Estilo.NEGRITA);
        lblMochila.setBounds(20, 450, 700, 20);
        add(lblMochila);
        pnlMochila.setBounds(20, 474, 700, 82);
        add(pnlMochila);

        JLabel lblTip = new JLabel("Consejo: el agua y la comida mantienen viva a la familia. "
                + "Las demás cosas desbloquean mejores decisiones en el refugio.");
        lblTip.setFont(Estilo.NORMAL);
        lblTip.setForeground(Estilo.TEXTO_SUAVE);
        lblTip.setBounds(20, 574, 920, 20);
        add(lblTip);
        JLabel lblTip2 = new JLabel("Cruzar una puerta cuesta " + GestorPartida.COSTO_MOVIMIENTO
                + " s. Una persona ocupa toda la mochila. Si el tiempo se acaba fuera del refugio, perdés.");
        lblTip2.setFont(Estilo.NORMAL);
        lblTip2.setForeground(Estilo.TEXTO_SUAVE);
        lblTip2.setBounds(20, 596, 920, 20);
        add(lblTip2);
    }

    /** Arranca la cuenta regresiva. Se llama cuando el panel ya está en pantalla. */
    public void comenzar() {
        SwingUtilities.invokeLater(() -> {
            refrescar();
            mostrarMensaje("Estás en el recibidor. ¡Buscá a tu familia!", Estilo.ACENTO);
        });
        timerJuego = new Timer(1000, e -> {
            if (gestor.avanzarSegundo()) terminar();
            else refrescarTiempo();
        });
        timerJuego.start();
    }

    // ---------------------------------------------------------------- acciones

    // La lógica decide si cada acción se puede hacer; si no, lanza una
    // AccionInvalidaException con el motivo y acá solo se muestra.

    private void agarrar(Cargable c) {
        if (terminado) return;
        try {
            gestor.recoger(c);
            mostrarMensaje(c instanceof Persona ? "¡" + c.getNombre() + " viene con vos!" : "+ " + c.getNombre(),
                    Estilo.EXITO);
        } catch (AccionInvalidaException ex) {
            mostrarMensaje(ex.getMessage(), Estilo.PELIGRO);
        }
        refrescar();
    }

    private void soltar(Cargable c) {
        if (terminado) return;
        try {
            gestor.soltar(c);
            mostrarMensaje((gestor.estaEnRefugio() ? "Guardaste: " : "Dejaste: ") + c.getNombre(), Estilo.TEXTO_SUAVE);
        } catch (AccionInvalidaException ex) {
            mostrarMensaje(ex.getMessage(), Estilo.PELIGRO);
        }
        refrescar();
    }

    private void salir(Salida s) {
        if (terminado) return;
        try {
            gestor.moverA(s);
            mostrarMensaje("-" + GestorPartida.COSTO_MOVIMIENTO + " s", Estilo.TEXTO_SUAVE);
        } catch (AccionInvalidaException ex) {
            mostrarMensaje(ex.getMessage(), Estilo.PELIGRO);
        }
        refrescar();
        if (gestor.getTiempoRestante() <= 0) terminar();
    }

    private void vaciar() {
        try {
            int n = gestor.vaciarMochilaEnRefugio();
            mostrarMensaje("Guardaste " + n + (n == 1 ? " cosa" : " cosas") + " en el refugio", Estilo.EXITO);
        } catch (AccionInvalidaException ex) {
            mostrarMensaje(ex.getMessage(), Estilo.PELIGRO);
        }
        refrescar();
    }

    private void cerrarEscotilla() {
        int faltan = gestor.getFamiliaresPerdidos().size();
        String pregunta = "¿Cerrar la escotilla ahora?\nTe quedan " + gestor.getTiempoRestante() + " segundos.";
        if (faltan > 0) {
            pregunta += "\n\n" + (faltan == 1 ? "Todavía falta 1 familiar." : "Todavía faltan " + faltan + " familiares.");
        }
        int r = JOptionPane.showConfirmDialog(this, pregunta, "Cerrar la escotilla",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) terminar();
    }

    /** Frena el reloj sin terminar la fase (cuando se abandona la partida desde el menú). */
    public void detener() {
        terminado = true;
        if (timerJuego != null) timerJuego.stop();
        if (timerMensaje != null) timerMensaje.stop();
    }

    private void terminar() {
        if (terminado) return; // el reloj y el botón podrían llamarlo a la vez
        terminado = true;
        timerJuego.stop();
        if (timerMensaje != null) timerMensaje.stop();
        ventana.terminarRecoleccion();
    }

    // ---------------------------------------------------------------- pantalla

    private void refrescar() {
        boolean enRefugio = gestor.estaEnRefugio();
        pnlEscena.mostrar(gestor.getHabitacionActual(), gestor.getProtagonista(),
                enRefugio ? gestor.getInventario().getItemsObtenidos() : null, this::agarrar, this::salir);
        plano.mostrar(gestor.getHabitaciones(), gestor.getHabitacionActual());
        pnlMochila.mostrar(gestor.getMochila(), this::soltar);

        Mochila m = gestor.getMochila();
        lblMochila.setText("MOCHILA  " + m.getEspacioUsado() + "/" + Mochila.CAPACIDAD + " espacios"
                + (m.estaVacia() ? "" : "   ·   clic en algo para dejarlo"));
        lblMochila.setForeground(m.getEspacioLibre() == 0 ? Estilo.ACENTO : Estilo.TEXTO);

        for (int i = 0; i < lblsFamilia.length; i++) {
            Persona p = gestor.getFamilia().get(i);
            boolean aSalvo = gestor.getFamiliaresEnRefugio().contains(p);
            boolean cargado = m.getContenido().contains(p);
            lblsFamilia[i].setText(p.getNombre() + (aSalvo ? "  ✓ a salvo" : cargado ? "  · con vos" : "  · falta"));
            lblsFamilia[i].setForeground(aSalvo ? Estilo.EXITO : cargado ? Estilo.ACENTO : Estilo.TEXTO_SUAVE);
        }

        btnVaciar.setVisible(enRefugio);
        btnVaciar.setEnabled(!m.estaVacia());
        btnCerrar.setVisible(enRefugio);
        lblAyudaRefugio.setVisible(!enRefugio);
        refrescarTiempo();
    }

    private void refrescarTiempo() {
        int t = gestor.getTiempoRestante();
        barTiempo.setValue(t);
        barTiempo.setString(t + " s");
        barTiempo.setForeground(t <= 10 ? Estilo.PELIGRO : t <= 25 ? Estilo.ACENTO : Estilo.EXITO);

        // Aviso si apenas queda tiempo para volver
        boolean urgente = !gestor.estaEnRefugio() && t <= gestor.segundosHastaRefugio() + 6;
        lblAviso.setVisible(urgente);
        if (urgente) {
            lblAviso.setText("¡VOLVÉ AL REFUGIO!");
            lblAviso.setBackground(t % 2 == 0 ? Estilo.PELIGRO : Estilo.PELIGRO.darker());
        }
    }

    private void mostrarMensaje(String texto, Color color) {
        lblMensaje.setText("<html><center>" + texto + "</center></html>");
        lblMensaje.setForeground(color);
        if (timerMensaje != null) timerMensaje.stop();
        timerMensaje = new Timer(2200, e -> lblMensaje.setText(""));
        timerMensaje.setRepeats(false);
        timerMensaje.start();
    }
}

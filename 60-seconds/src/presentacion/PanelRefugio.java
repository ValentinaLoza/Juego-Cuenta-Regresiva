package presentacion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import logica.AccionInvalidaException;
import logica.EstadoPartida;
import logica.GestorPartida;
import logica.Inventario;
import logica.Opcion;
import logica.Persona;
import logica.Refugio;
import logica.TipoItem;
import presentacion.componentes.Estilo;
import presentacion.componentes.PanelConFondo;
import presentacion.componentes.Recursos;
import presentacion.componentes.TarjetaFamiliar;

/** Fase 2: los días en el refugio. */
public class PanelRefugio extends PanelConFondo {

    private final FrmPrincipal ventana;
    private final GestorPartida gestor;
    private boolean modoNoche; // true = repartiendo raciones; false = decidiendo el evento

    private final JLabel lblDia = new JLabel();
    private final JLabel lblIntegridad = new JLabel();
    private final JLabel lblRadio = new JLabel();
    private final JLabel[] lblsInventario;
    private final JTextArea txaHistoria = new JTextArea();
    private final JButton[] botonesOpcion = new JButton[4];
    private final List<TarjetaFamiliar> tarjetas = new ArrayList<>();
    private final JLabel lblRaciones = new JLabel();
    private final JButton btnPasarNoche = Estilo.boton("Pasar la noche ➔", Estilo.ACENTO, Color.BLACK);

    public PanelRefugio(FrmPrincipal ventana, GestorPartida gestor) {
        super(Recursos.imagenHabitacion("REF"), 240);
        this.ventana = ventana;
        this.gestor = gestor;

        // --- Encabezado ---
        lblDia.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblDia.setForeground(Color.WHITE);
        lblDia.setBounds(20, 14, 320, 36);
        add(lblDia);
        lblIntegridad.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblIntegridad.setBounds(360, 18, 240, 28);
        add(lblIntegridad);
        lblRadio.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblRadio.setForeground(Estilo.ACENTO);
        lblRadio.setIcon(Recursos.iconoItem(TipoItem.RADIO, 26));
        lblRadio.setBounds(610, 16, 330, 32);
        add(lblRadio);

        // --- Inventario ---
        JPanel pnlInventario = new JPanel(null);
        pnlInventario.setBackground(Estilo.FONDO_PANEL);
        pnlInventario.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        pnlInventario.setBounds(20, 64, 200, 372);
        JLabel lblInv = new JLabel("PROVISIONES");
        lblInv.setFont(Estilo.NEGRITA);
        lblInv.setForeground(Color.WHITE);
        lblInv.setBounds(12, 8, 150, 20);
        pnlInventario.add(lblInv);
        TipoItem[] tipos = TipoItem.values();
        lblsInventario = new JLabel[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            JLabel lbl = new JLabel("", Recursos.iconoItem(tipos[i], 24), SwingConstants.LEFT);
            lbl.setIconTextGap(8);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setToolTipText(tipos[i].getNombre());
            lbl.setBounds(10, 32 + i * 28, 186, 27);
            lblsInventario[i] = lbl;
            pnlInventario.add(lbl);
        }
        add(pnlInventario);

        JLabel lblReglas = new JLabel("<html><div style='color:#a0a0aa;font-size:10px'>"
                + "Cada noche baja el agua y la comida de cada uno. Una ración entera llena la barra; "
                + "media, la mitad. Si una barra llega a 0, pierde salud.<br><br>"
                + "Barras: <font color='#6ed26e'>salud</font>, <font color='#5aaaf0'>agua</font>, "
                + "<font color='#dc783c'>comida</font>.<br>"
                + "Botones: — → ½ → 1</div></html>");
        lblReglas.setVerticalAlignment(SwingConstants.TOP);
        lblReglas.setBounds(22, 446, 198, 150);
        add(lblReglas);

        // --- Historia y decisiones ---
        txaHistoria.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txaHistoria.setBackground(new Color(18, 18, 20));
        txaHistoria.setForeground(Estilo.TEXTO);
        txaHistoria.setLineWrap(true);
        txaHistoria.setWrapStyleWord(true);
        txaHistoria.setEditable(false);
        txaHistoria.setMargin(new Insets(12, 12, 12, 12));
        JScrollPane scroll = new JScrollPane(txaHistoria);
        scroll.setBounds(235, 64, 455, 330);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        add(scroll);

        for (int i = 0; i < 4; i++) {
            JButton b = Estilo.boton("", new Color(70, 70, 80), Color.WHITE);
            b.setFont(Estilo.NEGRITA);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setIconTextGap(8);
            b.setBounds(235 + (i % 2) * 230, 404 + (i / 2) * 64, 225, 58);
            b.setVisible(false);
            final int indice = i;
            b.addActionListener(e -> elegirOpcion(indice));
            botonesOpcion[i] = b;
            add(b);
        }

        // --- Familia ---
        int y = 64;
        for (Persona p : gestor.getRefugio().getPersonas()) {
            TarjetaFamiliar t = new TarjetaFamiliar(p,
                    () -> cambiarRacion(p, TipoItem.AGUA),
                    () -> cambiarRacion(p, TipoItem.COMIDA),
                    () -> curar(p));
            t.setBounds(705, y, 235, 112);
            tarjetas.add(t);
            add(t);
            y += 118;
        }

        // --- Pie ---
        lblRaciones.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblRaciones.setForeground(Color.WHITE);
        lblRaciones.setBounds(235, 560, 455, 56);
        add(lblRaciones);

        btnPasarNoche.setBounds(705, 562, 235, 58);
        btnPasarNoche.addActionListener(e -> pasarNoche());
        add(btnPasarNoche);

        txaHistoria.setText(textoInicial());
        modoNoche = true;
        refrescar();
    }

    private String textoInicial() {
        StringBuilder sb = new StringBuilder("Cerrás la escotilla justo cuando la onda expansiva sacude el suelo.\n\n");
        List<Persona> aSalvo = gestor.getFamiliaresEnRefugio();
        List<Persona> perdidos = gestor.getFamiliaresPerdidos();
        if (perdidos.isEmpty()) {
            sb.append("Están todos juntos: ");
        } else if (aSalvo.isEmpty()) {
            sb.append("Estás solo. ");
        } else {
            sb.append("Con vos están: ");
        }
        for (int i = 0; i < aSalvo.size(); i++) {
            sb.append(aSalvo.get(i).getNombre()).append(i < aSalvo.size() - 1 ? ", " : ".");
        }
        if (!perdidos.isEmpty()) {
            sb.append("\n\nNo pudiste traer a ");
            for (int i = 0; i < perdidos.size(); i++) {
                sb.append(perdidos.get(i).getNombre()).append(i < perdidos.size() - 1 ? " ni a " : ".");
            }
            sb.append(" No dejás de pensar en eso.");
        }
        int objetos = gestor.getInventario().getCantidadTotal();
        sb.append("\n\nLograste guardar ").append(objetos).append(objetos == 1 ? " objeto." : " objetos.");
        sb.append("\n\nCada noche elegí a quién darle agua y comida en las tarjetas de la derecha. "
                + "Podés dar media ración: la botella o la lata abierta queda para otro.");
        return sb.toString();
    }

    // ---------------------------------------------------------------- acciones

    private void pasarNoche() {
        if (gestor.getEstado() != EstadoPartida.EN_CURSO) {
            mostrarFinal();
            return;
        }
        String resumen = gestor.procesarNuevoDia();
        if (gestor.getEstado() != EstadoPartida.EN_CURSO) {
            txaHistoria.setText("DÍA " + gestor.getRefugio().getDiasSupervividos() + "\n\n" + resumen);
            terminar();
            return;
        }
        txaHistoria.setText("DÍA " + gestor.getRefugio().getDiasSupervividos() + "\n\n" + resumen + "\n"
                + gestor.getEventoActual().getHistoria(gestor.getObjetivoEvento()));
        modoNoche = false;
        refrescar();
    }

    private void elegirOpcion(int indice) {
        Opcion op = gestor.getEventoActual().getOpciones().get(indice);
        String descripcion = op.getDescripcion(gestor.getObjetivoEvento());
        String resultado;
        try {
            resultado = gestor.ejecutarOpcion(op);
        } catch (AccionInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se puede", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txaHistoria.append("\n\n➔ " + descripcion + "\n" + resultado);
        if (gestor.getEstado() != EstadoPartida.EN_CURSO) {
            terminar();
            return;
        }
        modoNoche = true;
        refrescar();
    }

    private void curar(Persona p) {
        try {
            gestor.usarBotiquin(p);
            txaHistoria.append("\n\nUsaste un botiquín con " + p.getNombre() + " (+" + GestorPartida.CURACION_BOTIQUIN + " salud).");
        } catch (AccionInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se puede curar", JOptionPane.WARNING_MESSAGE);
        }
        refrescar();
    }

    private void cambiarRacion(Persona p, TipoItem tipo) {
        try {
            gestor.ciclarRacion(p, tipo);
        } catch (AccionInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Raciones", JOptionPane.INFORMATION_MESSAGE);
        }
        refrescar();
    }

    private void terminar() {
        modoNoche = true;
        refrescar();
        btnPasarNoche.setText("Ver el final ➔");
        btnPasarNoche.setEnabled(true);
        btnPasarNoche.setVisible(true);
        lblRaciones.setText("");
        for (TarjetaFamiliar t : tarjetas) t.actualizar(false, false, false, false);
    }

    private void mostrarFinal() {
        Refugio r = gestor.getRefugio();
        List<Persona> todos = new ArrayList<>();
        todos.add(gestor.getProtagonista());
        todos.addAll(gestor.getFamilia());
        List<Persona> vivos = r.getVivos();
        StringBuilder sb = new StringBuilder();
        if (gestor.getEstado() == EstadoPartida.VICTORIA) {
            String titulo = r.getContactosRadio() >= Refugio.CONTACTOS_PARA_RESCATE ? "¡RESCATADOS!" : "¡SOBREVIVIERON!";
            if (r.getContactosRadio() >= Refugio.CONTACTOS_PARA_RESCATE) {
                sb.append("Un camión militar se detiene frente a la casa. Gracias a la radio supieron dónde encontrarlos.\n\n");
            } else {
                sb.append("Después de ").append(r.getDiasSupervividos()).append(" días, el ejército llega a la zona y abre la escotilla.\n\n");
            }
            sb.append(vivos.size() == 4 ? "¡Toda la familia sobrevivió!" :
                      vivos.size() == 1 ? "Saliste solo a la luz del día." :
                      "Sobrevivieron " + vivos.size() + " de 4.");
            ventana.mostrarFinal(true, titulo, sb.toString(), todos);
        } else {
            sb.append(r.getIntegridad() <= 0 ? "El refugio se derrumbó." : "Nadie resistió en el refugio.");
            sb.append("\nAguantaron ").append(r.getDiasSupervividos()).append(" días.");
            ventana.mostrarFinal(false, "FIN DEL JUEGO", sb.toString(), todos);
        }
    }

    // ---------------------------------------------------------------- pantalla

    private void refrescar() {
        Refugio r = gestor.getRefugio();
        Inventario inv = gestor.getInventario();
        lblDia.setText("DÍA " + r.getDiasSupervividos() + " / " + Refugio.DIAS_PARA_RESCATE);
        lblIntegridad.setText("⌂ Refugio: " + r.getIntegridad() + "%");
        lblIntegridad.setForeground(r.getIntegridad() > 50 ? new Color(110, 200, 240) : Estilo.colorSegunValor(r.getIntegridad()));
        lblRadio.setText("Contactos por radio: " + r.getContactosRadio() + "/" + Refugio.CONTACTOS_PARA_RESCATE);

        TipoItem[] tipos = TipoItem.values();
        for (int i = 0; i < tipos.length; i++) {
            int n = tipos[i].esRacion() ? inv.getMitades(tipos[i]) : inv.contar(tipos[i]);
            String cantidad = tipos[i].esRacion() ? Inventario.textoMitades(n) : String.valueOf(n);
            lblsInventario[i].setText(tipos[i].getNombre() + "  x" + cantidad);
            lblsInventario[i].setForeground(n > 0 ? Estilo.TEXTO : new Color(105, 105, 112));
            lblsInventario[i].setEnabled(n > 0);
        }

        // Raciones (en mitades): se puede tocar el botón si hay algo para repartir
        int agua = inv.getMitades(TipoItem.AGUA), comida = inv.getMitades(TipoItem.COMIDA);
        int aguaElegida = gestor.mitadesElegidas(TipoItem.AGUA);
        int comidaElegida = gestor.mitadesElegidas(TipoItem.COMIDA);
        boolean enCurso = gestor.getEstado() == EstadoPartida.EN_CURSO;
        for (TarjetaFamiliar t : tarjetas) {
            Persona p = t.getPersona();
            // Se puede tocar si a esta persona ya se le dio algo, o si queda algo sin repartir
            boolean puedeAgua = p.getRacionAgua() > 0 || aguaElegida < agua;
            boolean puedeComida = p.getRacionComida() > 0 || comidaElegida < comida;
            t.actualizar(modoNoche && enCurso, puedeAgua, puedeComida,
                    enCurso && inv.tieneRecurso(TipoItem.MEDICAMENTO));
        }

        // Decisiones del evento
        boolean decidiendo = !modoNoche && gestor.getEventoActual() != null;
        if (decidiendo) cargarOpciones();
        else for (JButton b : botonesOpcion) b.setVisible(false);

        btnPasarNoche.setVisible(modoNoche);
        if (modoNoche && enCurso) {
            lblRaciones.setText("<html>Esta noche repartís: <font color='#5aaaf0'>" + Inventario.textoMitades(aguaElegida)
                    + " de " + Inventario.textoMitades(agua) + " aguas</font> · <font color='#dc783c'>"
                    + Inventario.textoMitades(comidaElegida) + " de " + Inventario.textoMitades(comida) + " latas</font></html>");
        } else if (decidiendo) {
            lblRaciones.setText("<html><font color='#ffc846'>Elegí qué hacer.</font></html>");
        }
    }

    private void cargarOpciones() {
        List<Opcion> opciones = gestor.getEventoActual().getOpciones();
        Persona objetivo = gestor.getObjetivoEvento();
        for (int i = 0; i < botonesOpcion.length; i++) {
            JButton b = botonesOpcion[i];
            if (i >= opciones.size()) {
                b.setVisible(false);
                continue;
            }
            Opcion op = opciones.get(i);
            String texto = op.getDescripcion(objetivo);
            if (op.getProbabilidadExito() < 100) texto += " (" + op.getProbabilidadExito() + "%)";
            boolean disponible = op.esEjecutable(gestor.getInventario());
            b.setEnabled(disponible);
            b.setBackground(disponible ? new Color(70, 70, 80) : new Color(40, 40, 44));
            b.setIcon(op.getRequerimiento() == null ? null : Recursos.iconoItem(op.getRequerimiento(), 30));
            if (!disponible) texto += "<br><font color='#e66a5a'>Necesitás: " + op.getRequerimiento().getNombre() + "</font>";
            b.setText("<html>" + texto + "</html>");
            b.setVisible(true);
        }
    }
}

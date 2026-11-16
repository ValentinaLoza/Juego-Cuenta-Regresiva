package presentacion;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import logica.RegistroPartida;
import logica.ServicioHistorial;
import presentacion.componentes.Estilo;

/**
 * Historial de partidas: listado en una tabla, búsqueda por jugador,
 * renombrar y eliminar. Todas las operaciones se piden a ServicioHistorial;
 * esta pantalla solo muestra datos y mensajes.
 */
public class PanelHistorial extends JPanel {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ServicioHistorial servicio;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtBuscar = new JTextField();
    private final JLabel lblResumen = new JLabel();
    private List<RegistroPartida> mostradas;

    public PanelHistorial(FrmPrincipal ventana, ServicioHistorial servicio) {
        super(null);
        this.servicio = servicio;
        setBackground(Estilo.FONDO);

        JLabel lblTitulo = new JLabel("HISTORIAL DE PARTIDAS");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(Estilo.ACENTO);
        lblTitulo.setBounds(30, 20, 600, 40);
        add(lblTitulo);

        lblResumen.setFont(Estilo.NORMAL);
        lblResumen.setForeground(Estilo.TEXTO_SUAVE);
        lblResumen.setBounds(30, 60, 900, 20);
        add(lblResumen);

        // --- Búsqueda ---
        JLabel lblBuscar = new JLabel("Buscar jugador:");
        lblBuscar.setFont(Estilo.NEGRITA);
        lblBuscar.setForeground(Estilo.TEXTO);
        lblBuscar.setBounds(30, 98, 120, 30);
        add(lblBuscar);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtBuscar.setBounds(150, 98, 300, 32);
        txtBuscar.addActionListener(e -> buscar()); // Enter también busca
        add(txtBuscar);
        JButton btnBuscar = Estilo.boton("Buscar", Estilo.ACENTO, Color.BLACK);
        btnBuscar.setBounds(460, 96, 110, 36);
        btnBuscar.addActionListener(e -> buscar());
        add(btnBuscar);
        JButton btnTodas = Estilo.boton("Ver todas", new Color(70, 70, 80), Color.WHITE);
        btnTodas.setBounds(580, 96, 120, 36);
        btnTodas.addActionListener(e -> { txtBuscar.setText(""); buscar(); });
        add(btnTodas);

        // --- Tabla ---
        String[] columnas = {"N°", "Jugador", "Fecha", "Resultado", "Días", "Sobrevivientes", "Puntaje"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFont(Estilo.NORMAL);
        tabla.getTableHeader().setFont(Estilo.NEGRITA);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(190);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 145, 900, 380);
        add(scroll);

        // --- Acciones ---
        JButton btnRenombrar = Estilo.boton("Cambiar nombre", new Color(70, 70, 80), Color.WHITE);
        btnRenombrar.setBounds(30, 545, 180, 50);
        btnRenombrar.addActionListener(e -> renombrar());
        add(btnRenombrar);

        JButton btnEliminar = Estilo.boton("Eliminar", Estilo.PELIGRO, Color.WHITE);
        btnEliminar.setBounds(220, 545, 140, 50);
        btnEliminar.addActionListener(e -> eliminar());
        add(btnEliminar);

        JButton btnJugar = Estilo.boton("Nueva partida", Estilo.ACENTO, Color.BLACK);
        btnJugar.setBounds(590, 545, 170, 50);
        btnJugar.addActionListener(e -> ventana.mostrarAdvertencias());
        add(btnJugar);

        JButton btnVolver = Estilo.boton("Portada", new Color(70, 70, 80), Color.WHITE);
        btnVolver.setBounds(770, 545, 160, 50);
        btnVolver.addActionListener(e -> ventana.mostrarPortada());
        add(btnVolver);

        buscar();
    }

    private void buscar() {
        mostradas = servicio.buscarPorJugador(txtBuscar.getText());
        modelo.setRowCount(0);
        for (RegistroPartida p : mostradas) {
            modelo.addRow(new Object[]{
                p.getId(), p.getJugador(), p.getFecha().format(FORMATO_FECHA), p.getResultado(),
                p.getDiasSobrevividos(), p.getSobrevivientes() + " de 4", p.getPuntaje()
            });
        }
        RegistroPartida mejor = servicio.mejorPartida();
        String resumen = servicio.cantidadPartidas() + " partidas guardadas";
        if (mejor != null) resumen += "   ·   Mejor puntaje: " + mejor.getPuntaje() + " (" + mejor.getJugador() + ")";
        if (!txtBuscar.getText().trim().isEmpty()) resumen += "   ·   Encontradas: " + mostradas.size();
        lblResumen.setText(resumen);
        if (mostradas.isEmpty() && !txtBuscar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidas de \"" + txtBuscar.getText().trim() + "\".",
                    "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** La partida elegida en la tabla, o null (avisando) si no hay ninguna. */
    private RegistroPartida seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Primero elegí una partida de la tabla.",
                    "Ninguna partida elegida", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return mostradas.get(fila);
    }

    private void renombrar() {
        RegistroPartida p = seleccionada();
        if (p == null) return;
        String nuevo = (String) JOptionPane.showInputDialog(this, "Nuevo nombre del jugador:",
                "Cambiar nombre", JOptionPane.PLAIN_MESSAGE, null, null, p.getJugador());
        if (nuevo == null) return; // canceló
        try {
            servicio.renombrar(p.getId(), nuevo);
            buscar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Nombre no válido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        RegistroPartida p = seleccionada();
        if (p == null) return;
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar la partida N° " + p.getId() + " de " + p.getJugador() + "?",
                "Eliminar partida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            servicio.eliminar(p.getId());
            buscar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
